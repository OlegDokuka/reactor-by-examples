import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.util.Loggers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

public class Solution {

	public static void main(String[] args) throws InterruptedException {
//		Flux<Long> streamFibonacci1 = generateFibonacciSequenceTill(5);
//		streamFibonacci1
//				.log()
//				.subscribe();

//
//		System.out.println("---one more---");
//
//		streamFibonacci1
//				.log()
//				.subscribe();
//
//		System.exit(-9);

		for (int i = 0; i < 1000; i++) {
			Flux<Long> streamFibonacci = generateFibonacciSequenceTill(5);
			CountDownLatch cdl = new CountDownLatch(2);

			int localI = i;

			new Thread(() -> {
				cdl.countDown();
				try {
					cdl.await();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
				streamFibonacci
						.log(localI + " r1")
						.count()
						.block();
			}).start();


//			System.out.println("---one more---");

			new Thread(() -> {
				cdl.countDown();
				try {
					cdl.await();
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}

				streamFibonacci
						.log(localI + " r2")
						.count()
						.block();
			}).start();

		}

		Thread.sleep(1111);
	}



	static Flux<Long> generateFibonacciSequenceTill(int level) {
		// synchronous sequence generator
		return Flux.<Long, Tuple2<Long, Long>>generate(() -> Tuples.of(0L, 1L), (Tuple2<Long, Long> state, SynchronousSink<Long> simpleSubscriber) -> {
			simpleSubscriber.next(state.getT1());
			// next state
			return Tuples.of(state.getT2(), state.getT1() + state.getT2());
		}).take(level + 1);
	}
}

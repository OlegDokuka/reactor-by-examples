import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

public class Problem {


	public static void main(String[] args) throws InterruptedException {
		Stream<Long> streamFibonacci1 = generateFibonacciSequenceTill(5);

//		streamFibonacci1.forEach(System.out::println);

//		System.exit(-9);

		for (int i = 0; i < 1000; i++) {
			Stream<Long> streamFibonacci = generateFibonacciSequenceTill(5);
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
						.peek(x -> System.out.println(x + " " + localI))
						.count();
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
						.peek(x -> System.out.println(x + " " + localI))
						.count();
			}).start();

		}

		Thread.sleep(1000);
	}



	static Stream<Long> generateFibonacciSequenceTill(int level) {
		return Stream.generate(new Supplier<Long>() {
			long previous = 0;
			long current = 1;

			@Override
			public Long get() {
				long temp = previous;
				previous = current;
				current = temp + current;

				return temp;
			}
		}).limit(level + 1);
	}
}

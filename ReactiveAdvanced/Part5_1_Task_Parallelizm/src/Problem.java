import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class Problem {


	public static void main(String[] args) throws InterruptedException {
		long start = System.nanoTime();

		Flux.range(0, 100)
			.delayElements(Duration.ofMillis(10))
			.doOnNext((value) -> {
				System.out.println(Thread.currentThread() + " Doing Processing 1 " + value);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			})
			.doOnNext((value) -> {
				System.out.println(Thread.currentThread() + " Doing Processing 2 " + value);
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			})
			.log("result")
			.blockLast();

		long end = System.nanoTime();

		System.out.println("Execution time is " + (end - start) / 1_000_000d );

	}

}

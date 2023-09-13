import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;

public class Solution {


	public static void main(String[] args) throws InterruptedException {
		long start = System.nanoTime();
		Flux.range(0, 100)
				.delayElements(Duration.ofMillis(10), Schedulers.single())
				.doOnNext((value) -> {
					System.out.println(Thread.currentThread() + " Doing Processing 1 " + value);

					try {
						Thread.sleep(10);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				})
				.publishOn(Schedulers.parallel()) // async boundary - new worker responsible for processing of the rest of the tasks
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

import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;

public class Solution {


	public static void main(String[] args) throws InterruptedException {
		CountDownLatch cdl = new CountDownLatch(1);
		Flux.interval(Duration.ofMillis(10))
				.log("a")
				.publishOn(Schedulers.parallel()) // introduces async boundary
				.log("b")
				.doFinally(__ -> cdl.countDown())
				.subscribe(event -> {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						throw new RuntimeException(e);
					}
				});

		cdl.await();
	}

	static Flux<Integer> queryData() {
		return Flux.defer(() -> {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}

			return Flux.fromIterable(Arrays.asList(25, 62, 12312));
		})
				.subscribeOn(Schedulers.boundedElastic());
	}

}

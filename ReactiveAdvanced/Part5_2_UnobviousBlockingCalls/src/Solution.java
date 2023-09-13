import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;

public class Solution {


	public static void main(String[] args) throws InterruptedException {
		Flux<Integer> sourceOne = queryData();
		Flux<Integer> sourceTwo = Flux.range(0, 100);

		Flux.merge(sourceOne, sourceTwo)
			.log("before")
			.publishOn(Schedulers.parallel())
			.log("after")
			.blockLast();
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

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;

public class Solution {


	public static void main(String[] args) throws InterruptedException {
		Flux.error(new Throwable())
				.log()
				.retryWhen(
					Retry.backoff(10, Duration.ofMillis(100))
						 .maxBackoff(Duration.ofSeconds(10))
				)
				.timeout(Duration.ofSeconds(10), Mono.just(1))
				.blockLast();
	}

}

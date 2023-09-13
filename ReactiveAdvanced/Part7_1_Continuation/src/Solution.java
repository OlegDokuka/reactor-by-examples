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
		var constantValue = 10;
//		Flux.range(0, 100)
//			.map(i -> constantValue / i)
//			.onErrorResume(t -> Mono.empty())
//			.log()
//			.blockLast();

		Flux.<Integer>error(new ArithmeticException())
			.map(i -> constantValue / i)
			.onErrorContinue((exception, element) -> {})
			.log()
			.blockLast();
	}

}

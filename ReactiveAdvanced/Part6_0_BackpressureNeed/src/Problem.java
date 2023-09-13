import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class Problem {

	//  [*]           [ ]          [*]
	//  [*]           [ ]          [*]
	//  [*]           [ ]   r(3)   [*]
	//  [*]    ==>>   [*]   ==>>   [*]
	//  [*]           [*]          [*]
	//  [*]           [*]          [*]
	//  [*]           [*]          [*]

	public static void main(String[] args) throws InterruptedException {
		CountDownLatch cdl = new CountDownLatch(1);
		Flux.range(0, 10000)
			.delayElements(Duration.ofMillis(10))
			.log("a")
			.publishOn(Schedulers.boundedElastic(), 32) // introduces async boundary
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
}

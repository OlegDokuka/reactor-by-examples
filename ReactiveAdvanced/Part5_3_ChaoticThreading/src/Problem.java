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


	public static void main(String[] args) throws InterruptedException {
		Flux.range(0, 100)
			.publishOn(Schedulers.parallel())
//			.log("a")
//			.flatMap(event -> {
//						return Mono.just(Arrays.asList(event)).log("we are processing event[" + event + "]", Level.INFO, SignalType.ON_NEXT);
//			})
			.flatMap(event -> {
				if (ThreadLocalRandom.current().nextBoolean()) {
					return queryData(event)/*.log("we are processing event[" + event + "]", Level.INFO, SignalType.ON_NEXT)*/;
				}
				return Mono.just(Arrays.asList(event))/*.log("we are processing event[" + event + "]", Level.INFO, SignalType.ON_NEXT)*/;
			})
			.log("b")
			.blockLast();
	}


	static Mono<List<Integer>> queryData(Integer event) {
		return Mono.defer(() -> {
//			try {
//				Thread.sleep(2000);
//			} catch (InterruptedException e) {
//				throw new RuntimeException(e);
//			}

			return Mono.just(Arrays.asList(25 * event, 62 * event, 12312 * event));
		}).subscribeOn(Schedulers.boundedElastic());
	}
}

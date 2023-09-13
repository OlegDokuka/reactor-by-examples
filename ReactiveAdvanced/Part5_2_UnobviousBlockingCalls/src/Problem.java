import java.time.Duration;
import java.util.*;
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
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class Problem {


	public static void main(String[] args) throws InterruptedException {
		Flux<Integer> sourceOne = queryData();
		Flux<Integer> sourceTwo = Flux.range(0, 100).publishOn(Schedulers.boundedElastic());

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
		});
	}
}

import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuples;

public class Problem {


	public static void main(String[] args) throws InterruptedException {
//		Flux.merge(
//				Flux.range(0, Integer.MAX_VALUE).log("range_1").subscribeOn(Schedulers.boundedElastic()),
//				Flux.just(-1).log("range_2")
//		).take(512)
//						.blockLast();
		Mono.empty()
				.log("first")
				.delaySubscription(Duration.ofMillis(100))
				.zipWith(Mono.delay(Duration.ofSeconds(1)).log("second"))
				.log("result")
				.block();
	}

}

import java.time.Duration;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.util.Loggers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

public class SolutionSlow {


	public static void main(String[] args) throws InterruptedException {
		Flux.range(0, 100)
			.log("before")
			.concatMap(SolutionSlow::makeHttpCallForData)
			.map(Objects::toString)
			.log("after")
			.blockLast();
	}

	static Mono<Object> makeHttpCallForData(long data) {
		return Mono.delay(Duration.ofMillis(ThreadLocalRandom.current().nextInt(100, 1000)))
				.thenReturn(data);
	}

}

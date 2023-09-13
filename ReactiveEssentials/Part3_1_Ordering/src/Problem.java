import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

public class Problem {


	public static void main(String[] args) throws InterruptedException {
		Flux.range(0, 100)
			.log("before")
			.flatMap(Problem::makeHttpCallForData)
			.map(Objects::toString)
			.log("after")
			.blockLast();
	}

	static Mono<Object> makeHttpCallForData(long data) {
		return Mono.delay(Duration.ofMillis(ThreadLocalRandom.current().nextInt(100, 1000)))
				.thenReturn(data);
	}

}

import java.time.Duration;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.function.Supplier;
import java.util.stream.Stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.function.Tuples;

public class Problem {


	public static void main(String[] args) throws InterruptedException {
		Flux.interval(Duration.ofSeconds(1))
			.log("before")
			.reduceWith(() -> Tuples.of(0L, 0L), (state, next) -> Tuples.of(state.getT1() + 1, state.getT2() + next))
			.map(t -> t.getT2() / t.getT1().doubleValue())
			.log("after")
			.block();
	}

}

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class SolutionFast {


	public static void main(String[] args) throws InterruptedException {
		Flux.range(0, 100)
			.log("before")
			.flatMapSequential(SolutionFast::makeHttpCallForData)
			.map(Objects::toString)
			.log("after")
			.blockLast();
	}

	static Mono<Object> makeHttpCallForData(long data) {
		return Mono.delay(Duration.ofMillis(ThreadLocalRandom.current().nextInt(100, 1000)))
				.thenReturn(data);
	}

}

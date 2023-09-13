import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

public class SolutionWithOptional {


	public static void main(String[] args) throws InterruptedException {
		Mono.empty()
			.log("first")
			.delaySubscription(Duration.ofMillis(100))
			.singleOptional()
			.zipWith(
					Mono.delay(Duration.ofSeconds(1)).log("second").singleOptional()
			)
			.log("result")
			.block();;
	}

}

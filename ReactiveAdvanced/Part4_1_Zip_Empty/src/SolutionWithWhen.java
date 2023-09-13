import reactor.core.publisher.Mono;

import java.time.Duration;

public class SolutionWithWhen {


	public static void main(String[] args) throws InterruptedException {
		Mono.when(
				Mono.empty().log("first").delaySubscription(Duration.ofMillis(100)),
				Mono.delay(Duration.ofSeconds(3)).log("second")
			)
			.log("result")
			.block();
	}

}

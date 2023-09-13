import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Duration;

public class SolutionWithWindow {

	public static void main(String[] args) throws InterruptedException {
		Flux.interval(Duration.ofSeconds(1))
				.log("before")
				.window(Duration.ofSeconds(5))
				.concatMap(wf -> wf
						.reduceWith(() -> Tuples.of(0L, 0L), (state, next) -> Tuples.of(state.getT1() + 1, state.getT2() + next))
						.map(t -> t.getT2() / t.getT1().doubleValue())
				)
				.log("after")
				.blockLast();
	}
}

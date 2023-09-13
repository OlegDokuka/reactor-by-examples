import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Duration;

public class SolutionScan {


	public static void main(String[] args) throws InterruptedException {
		Flux.interval(Duration.ofSeconds(1))
				.log("before")
				.scanWith(() -> Tuples.of(0L, 0L), (state, next) -> {
					return Tuples.of(state.getT1() + 1, state.getT2() + next);
				})
				.skip(1)
				.map(t -> t.getT2() / t.getT1().doubleValue())
				.log("after")
				.blockLast();
	}

}

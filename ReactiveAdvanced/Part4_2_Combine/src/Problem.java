import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Duration;

public class Problem {


	public static void main(String[] args) {
		Flux<String> payments = Flux.interval(Duration.ofSeconds(1))
				.map(i -> "Payment{" + i + "}");

		Flux<String> exchangeRates = Flux.interval(Duration.ofSeconds(5))
				.map(i -> "ExchangeRate{" + i + "}")
				.startWith("ExchangeRate{-1}");

		Flux.combineLatest(payments, exchangeRates, (p, er) -> Tuples.of(p, er))
			.map(t -> "Execute " + t.getT1() + " using " + t.getT2())
			.log()
			.blockLast();
	}

}

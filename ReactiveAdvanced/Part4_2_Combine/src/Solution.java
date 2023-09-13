import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Collection;
import java.util.HashSet;

public class Solution {


	public static void main(String[] args) throws InterruptedException {
		Flux<String> payments = Flux.interval(Duration.ofSeconds(1))
				.map(i -> "Payment{" + i + "}");

		Flux<String> exchangeRates = Flux.interval(Duration.ofSeconds(5))
				.map(i -> "ExchangeRate{" + i + "}")
				.startWith("ExchangeRate{-1}");

		Flux.combineLatest(payments, exchangeRates, (p, er) -> Tuples.of(p, er))
			.distinct(Tuple2::getT1, HashSet::new, (hs, key) -> {
				boolean check = hs.contains(key);

				hs.clear();
				hs.add(key);

				return !check;
			}, Collection::clear)
			.map(t -> "Execute " + t.getT1() + " using " + t.getT2())
			.log()
			.blockLast();
	}

}

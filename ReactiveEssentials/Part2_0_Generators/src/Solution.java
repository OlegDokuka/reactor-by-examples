import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Logger;
import java.util.stream.Stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.Loggers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

public class Solution {

	public static void main(String[] args) {
		Flux<Long> streamFibonacci = generateFibonacciSequenceTill(5);

		streamFibonacci
				.log()
				.subscribe();

		System.out.println("---one more---");

		streamFibonacci
				.log()
				.subscribe();
	}



	static Flux<Long> generateFibonacciSequenceTill(int level) {
		return Flux.<Long, Tuple2<Long, Long>>generate(() -> Tuples.of(0L, 1L), (state, simpleSubscriber) -> {
			simpleSubscriber.next(state.getT1());
			return Tuples.of(state.getT2(), state.getT1() + state.getT2());
		})
				.take(level + 1);
	}
}

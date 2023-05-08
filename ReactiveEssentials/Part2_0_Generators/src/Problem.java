import java.util.ArrayList;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

public class Problem {


	public static void main(String[] args) {
		Stream<Long> streamFibonacci = generateFibonacciSequenceTill(5);

		streamFibonacci
				.peek(System.out::println)
				.count();


//		System.out.println("---one more---");

//		streamFibonacci
//				.peek(System.out::println)
//				.count();
	}



	static Stream<Long> generateFibonacciSequenceTill(int level) {
		return Stream.generate(new Supplier<Long>() {
			long previous = 0;
			long current = 1;

			@Override
			public Long get() {
				long temp = previous;
				previous = current;
				current = temp + current;

				return temp;
			}
		}).limit(level + 1);
	}
}

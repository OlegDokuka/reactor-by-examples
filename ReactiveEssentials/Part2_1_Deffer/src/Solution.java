import java.time.Duration;
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
		String query = "SELECT * FROM USER";

		Mono<Void> validateProcess = validate(query);
		Mono<Long> writeQueryProcess = writeQuery(query);

		validateProcess
				.doOnSuccess(__ -> {
					System.out.println("validation is done");
				})
				.then(writeQueryProcess)
				.doOnSuccess(written -> System.out.println("written rows" + written))
				.block();
	}


	/**
	 * For all placeholder such as {@code $1} you need to provide and object to bind
	 *
	 * @param query
	 * @param argsToBind
	 * @return
	 */
	static Mono<Long> writeQuery(String query, Object... argsToBind) {
		return Mono.defer(() -> {
			var boundQuery = bind(query, argsToBind);

			return Mono.delay(Duration.ofSeconds(1))
					.doOnSubscribe(__ -> {
						System.out.println("Execution " + boundQuery);
					});
		});
	}


	static String bind(String query, Object... argsToBind) {
		System.out.println("Binding args for query[" + query + "]");
		String resultQuery = query;

		int nextIndex = -1;
		int argIndex = 0;
		while ((nextIndex = resultQuery.indexOf("$")) > -1) {
			resultQuery = resultQuery.replaceFirst("\\$", argsToBind[argIndex].toString());
			argIndex++;
		}

		return resultQuery;
	}

	static Mono<Void> validate(String query) {
		return Mono.delay(Duration.ofSeconds(1))
				.doOnSubscribe(__ -> System.out.println("validation started"))
				.then(Mono.fromRunnable(() -> System.out.println("Validation completed"))); // - possible valid item
//				.then(Mono.error(new IllegalArgumentException("invalid")));  // possible expected - filtered value
	}
}

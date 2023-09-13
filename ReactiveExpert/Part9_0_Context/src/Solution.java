import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.time.Duration;

public class Solution {
	static final Logger logger =
			LoggerFactory.getLogger(Solution.class);

	static final WebClient client = WebClient.builder().build();

	public static void main(String[] mainArgs) {
		Flux.interval(Duration.ofMillis(10))
				.onBackpressureDrop()
				.map(id -> "User{" + id + "}")
				.flatMap(user -> {
					return call()
							.doOnNext(t -> logger.info("Requester user is " + user + " response containing user is " + t.getT1()))
							.contextWrite(Context.of("user", user));
				})
				.blockLast();
	}

	static Mono<Tuple3<String, String, String>> call() {
		return Mono.deferContextual(context ->
				Mono.zip(
						client.get()
								.uri("https://ru.wikipedia.org/wiki/Заглавная_страница")
								.retrieve()
								.bodyToMono(String.class),
						client.get()
								.uri("https://ru.wikipedia.org/wiki/Заглавная_страница")
								.retrieve()
								.bodyToMono(String.class)
								.onErrorResume(t -> Mono.delay(Duration.ofMillis(100), Schedulers.single()).then(Mono.empty()))
					)
					.map(response -> Tuples.of(context.get("user"), response.getT1(), response.getT2()))

				)
				/*.handle((response, sink) -> sink.next(Tuples.of(sink.contextView().get("user"), response.getT1(), response.getT2())))*/;
	}
}

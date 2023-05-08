import java.util.Map;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

public class Problem {


	static WebClient webClient = WebClient.create();

	public static void main(String[] args) {
		get()
				.log()
				.blockLast();
	}

	static Flux<Map.Entry<String, Object>> get() {
		return webClient.get()
		                .uri("https://httpbin.org/anything")
		                .retrieve()
		                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
		                })
						.flatMapIterable(Map::entrySet);
	}
}

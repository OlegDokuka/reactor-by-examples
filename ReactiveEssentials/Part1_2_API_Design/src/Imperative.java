import java.util.Map;
import java.util.Objects;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;

public class Imperative {

	static WebClient webClient = WebClient.create();

	public static void main(String[] args) {
		Map<String, Object> responseBody = get();

		System.out.println(responseBody);
	}

	static Map<String, Object> get() {
			return webClient.get()
			                .uri("https://httpbin.org/anything")
			                .retrieve()
							.bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {
							})
			                .block();
	}
}

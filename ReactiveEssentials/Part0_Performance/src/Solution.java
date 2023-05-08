import reactor.core.publisher.Mono;

import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

public class Solution {

	static WebClient webClient = WebClient.create();

	public static void main(String[] args) {
		System.out.println("starting");

		long start = System.nanoTime();

		Mono.zip(
				call(),
			    call(),
			    call()
		    )
		    .map(responses -> {
			    ResponseEntity<String> resp1 = responses.getT1();
			    ResponseEntity<String> resp2 = responses.getT2();
			    ResponseEntity<String> resp3 = responses.getT3();
				return resp1.toString() + resp2 + resp3;
		    })
			.log()
			.block();

		long end = System.nanoTime();

		System.out.format("Requests have taken %s ms\n\n", (end - start) / 1000_000);
	}

	static Mono<ResponseEntity<String>> call() {
		return webClient.get()
		                .uri("https://httpbin.org/anything")
		                .retrieve()
		                .toEntity(String.class);
	}
}

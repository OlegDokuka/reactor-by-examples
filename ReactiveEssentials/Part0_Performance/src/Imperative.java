import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

public class Imperative {


	static WebClient webClient = WebClient.create();

	public static void main(String[] args) {
		System.out.println("starting");

		long start = System.nanoTime();


		ResponseEntity<String> resp1 = call();
		ResponseEntity<String> resp2 = call();
		ResponseEntity<String> resp3 = call();

		System.out.println(resp1.toString() + resp2 + resp3);

		long end = System.nanoTime();


		System.out.format("Requests have taken %s ms\n\n", (end - start) / 1000_000);
	}

	static ResponseEntity<String> call() {
		return webClient.get()
		                .uri("https://httpbin.org/anything")
		                .retrieve()
		                .toEntity(String.class)
		                .block();
	}
}

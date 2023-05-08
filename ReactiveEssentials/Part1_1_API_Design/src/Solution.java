import reactor.core.publisher.Mono;

public class Solution {

	public static void main(String[] args) {
		validate(1)
				.switchIfEmpty(Mono.fromRunnable(() -> System.out.println("valid item")))
				.onErrorResume(error -> Mono.fromRunnable(() -> System.out.println("invalid item")))
				.block();
	}


	static Mono<Void> validate(int input) {
		return Mono.empty(); // - possible valid item
//		Mono.error(new IllegalArgumentException("invalid"))  // possible expected - filtered value
	}
}

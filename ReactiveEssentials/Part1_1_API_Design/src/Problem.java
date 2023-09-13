import reactor.core.publisher.Mono;

public class Problem {

	public static void main(String[] args) {
		validate(1)
				.flatMap(result -> {
					if (result) {
						// do action
						return Mono.fromCallable(() -> {
							System.out.println("valid item");
							return 1;
						});
					} else {
						// do action
						return Mono.fromRunnable(() -> System.out.println("invalid item"));
					}
				})
				.onErrorResume(error -> Mono.fromRunnable(() -> System.out.println("invalid item")))
				.switchIfEmpty(Mono.fromRunnable(() -> System.out.println("invalid item")))
				.block();
	}


	static Mono<Boolean> validate(int input) {
//		return Mono.just(true); // possible - passed
		return Mono.just(false); // possible - filtered
//		return Mono.just(null); // prohibited - filtered
//		return Mono.empty(); // ??? - possible but unexpected
//		Mono.error(new IllegalArgumentException("invalid"))  // possible expected -	usually filtered as well
	}
}

import reactor.core.publisher.Flux;

public class SolutionHandle {


	public static void main(String[] args) throws InterruptedException {
		var constantValue = 10;

		Flux.<Integer>error(new ArithmeticException())
			.handle((i, sink) -> {
				try {
					int result = constantValue / i;
					sink.next(result);
				} catch (ArithmeticException e) {
					// ignore this one
				} catch (Throwable e) {
					sink.error(e);
				}
			})
			.log()
			.blockLast();
	}

}

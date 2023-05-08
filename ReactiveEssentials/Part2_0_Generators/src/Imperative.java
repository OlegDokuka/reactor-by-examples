import java.util.ArrayList;

public class Imperative {

	public static void main(String[] args) {
		System.out.println(generateFibonacciSequenceTill(5));
	}

	static Iterable<Long> generateFibonacciSequenceTill(int level) {
		ArrayList<Long> sequence = new ArrayList<>();

		sequence.add(0L);

		long previous = 0;
		long current = 1;
		int index = 0;

		while (level > index) {
			sequence.add(current);

			long temp = previous;
			previous = current;
			current = temp + current;

			index++;
		}

		return sequence;
	}
}

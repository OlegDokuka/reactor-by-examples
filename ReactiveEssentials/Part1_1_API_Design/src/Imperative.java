public class Imperative {

	public static void main(String[] args) {
		if (validate(1)) {
			// do action
			System.out.println("valid item");
		} else {
			// do action
			System.out.println("invalid item");
		}
	}


	static boolean validate(int input) {
//		return true; // valid
		return false; // invalid
//		throw new IllegalArgumentException("invalid")  // ??? - possible but never used
	}
}

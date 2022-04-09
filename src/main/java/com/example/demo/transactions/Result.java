package com.example.demo.transactions;

public interface Result {

	static Result error(Throwable t, Transaction connection) {
		return new ErrorResult(t, connection);
	}

	static Result ok(Transaction connection) {
		return new SuccessResult(connection);
	}

	Throwable error();

	Transaction session();
}

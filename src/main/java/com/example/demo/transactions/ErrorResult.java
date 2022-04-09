package com.example.demo.transactions;

public class ErrorResult implements Result {

	private final Throwable error;
	private final Transaction connection;

	public ErrorResult(Throwable throwable, Transaction session) {
		this.error = throwable;
		this.connection = session;
	}

	@Override
	public Throwable error() {
		return error;
	}

	@Override
	public Transaction session() {
		return connection;
	}
}

package com.example.demo.transactions;

public class SuccessResult implements Result {

	private final Transaction connection;

	public SuccessResult(Transaction connection) {
		this.connection = connection;
	}

	@Override
	public Throwable error() {
		return null;
	}

	@Override
	public Transaction session() {
		return connection;
	}
}

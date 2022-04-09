package com.example.demo.transactions;

import reactor.core.publisher.Mono;

public interface DatabaseApi {

	Mono<Transaction> open();
}

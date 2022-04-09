package com.example.demo.transactions;

import org.reactivestreams.Publisher;
import reactor.core.publisher.Mono;

public interface Transaction {

	Mono<Void> commit();

	Mono<Void> rollback();

	Mono<Void> execute(String query);
}

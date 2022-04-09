package com.example.demo;

import java.time.Duration;
import java.util.Currency;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeoutException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.logging.Level;

import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;
import reactor.core.publisher.ParallelFlux;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class Example01 {

	public static void main(String[] args) throws InterruptedException {
		Flux<Integer> asyncOps = Flux.never();
		// onNext(signal(1)) -> onNext(signal(2)) -> onNext(signal(3)) -> onComplete()

		// Thread1 -> onNext(signal(1))
		// Thread2 ->     onNext(signal(2))
//		Flux.concat(
//			Flux.interval(Duration.ofSeconds(1)).map(tick -> tick + " from 1").take(10),
//			Flux.interval(Duration.ofMillis(500)).map(tick -> tick + " from 2").take(10),
//			Flux.interval(Duration.ofMillis(100)).map(tick -> tick + " from 3").take(10)
//		)
//				.next()
//				.block();

		Flux.range(0, 1256)
			.log("before groupBy")
		    .groupBy(i -> i % 512) // 512 groups should be created
		    .flatMap(groupedFlux -> {
			    System.out.println("Created group with a key " + groupedFlux.key());
				return groupedFlux.takeUntilOther(Mono.delay(Duration.ofMillis(1)));
		    })
//		    .take(288)
		    .as(StepVerifier::create)
		    .expectNextCount(1024)
		    .expectComplete()
		    .verify(Duration.ofSeconds(10));





	}

	static Mono<String> asyncCall(int i) {
		return Mono.delay(Duration.ofMillis(100))
		           .thenReturn("");
	}

}

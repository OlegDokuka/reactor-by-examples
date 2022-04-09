package com.example.demo;

import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;
import reactor.test.publisher.PublisherProbe;
import reactor.test.publisher.TestPublisher;
import reactor.test.scheduler.VirtualTimeScheduler;

public class ExampleTest {

	public static void main(String[] args) throws InterruptedException {
/*//    final TestPublisher<String> objectTestPublisher = TestPublisher.createCold();
//    objectTestPublisher.next("3");
//    objectTestPublisher.next("2");

//    StepVerifier.create(processAndFilter(objectTestPublisher.flux()))
//          .expectSubscription()
//          .expectNext(3, 2)
//          .then(() -> objectTestPublisher.assertWasSubscribed())
//          .then(() -> objectTestPublisher.next("A"))
//          .expectNoEvent(Duration.ofMillis(1))
//          .then(() -> objectTestPublisher.next("1"))
//          .expectNext(1)
//          .then(() -> objectTestPublisher.next("B"))
//          .expectNoEvent(Duration.ofMillis(1))
//          .then(() -> objectTestPublisher.assertMaxRequested(64))
//          .then(() -> objectTestPublisher.error(new RuntimeException()))
//          .expectComplete()
//          .verify();*/

		final PublisherProbe<String> sourceProbe = PublisherProbe.of(Flux.generate(sink -> {sink.next("a");}));

		StepVerifier.create(processAndFilter(
				sourceProbe.flux()
            )
                        .expectSubscription()
						.expectNoEvent(Duration.ofSeconds(1))
                        .thenCancel()
                        .verify();


	}

	static Flux<Integer> processAndFilter(Flux<String> rawInputFlux) {
		return rawInputFlux.publishOn(Schedulers.boundedElastic(), 64)
		                   .<Integer>handle((data, sink) -> {
			                   try {
				                   sink.next(Integer.parseInt(data));
			                   }
			                   catch (NumberFormatException e) {
			                   }
		                   })
		                   .onErrorResume(t -> Mono.empty());
	}


}

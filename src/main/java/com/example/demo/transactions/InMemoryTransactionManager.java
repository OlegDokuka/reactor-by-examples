package com.example.demo.transactions;

import java.time.Duration;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class InMemoryTransactionManager {

  static ConcurrentMap<String, String> data = new ConcurrentHashMap<>();

  static ConcurrentMap<String, Mono<String>> actions = new ConcurrentHashMap<>();

  public static void main(String[] args) {

    Mono<String> previousAction = Mono.empty();

    for (int i = 0; i < 100; i++) {
      final Mono<String> nextAction = Mono.<String>fromRunnable(() -> {})
          .delaySubscription(previousAction.then())
          .cache();
      previousAction = nextAction;

      nextAction.subscribe();

    }


    Flux.range(0, 100)
        .map(i -> "key" + (i % 5))
        .flatMap(key ->
            Mono.fromCallable(() -> data.get(key))
                .switchIfEmpty(callInTransaction(key, () -> readWrite(key)).subscribeOn(Schedulers.boundedElastic()))
        )
//        .flatMap(key -> Mono.fromCallable(() -> data.get(key)).switchIfEmpty(readWrite(key).subscribeOn(Schedulers.boundedElastic())))
        .blockLast();

    System.out.println(data);
  }


  static Mono<String> callInTransaction(String key, Supplier<? extends Mono<String>> queryCall) {
    return Mono.defer(() -> actions.compute(key,
        (__, previousMono) -> previousMono != null
            ? queryCall.get().delaySubscription(previousMono.then()).cache()
            : queryCall.get().cache()
        )
    );
  }


  static Mono<String> readWrite(String query) {
    return Mono
        .fromCallable(() -> data.get(query))
        .switchIfEmpty(
            Mono.defer(() ->
                  Mono.delay(Duration.ofMillis(ThreadLocalRandom.current().nextLong(1000, 10000)))
                      .map(i -> UUID.randomUUID() + query)
                )
                .doOnNext(s -> data.compute(query, (__, previous) -> previous == null ? s : previous + " " + s))
        )
        .log(query);
  }

}

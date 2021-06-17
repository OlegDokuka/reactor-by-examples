package com.example.demo;

import java.time.Duration;
import java.util.Collection;
import java.util.concurrent.ConcurrentMap;
import java.util.logging.Level;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;
import reactor.util.annotation.NonNull;
import reactor.util.retry.Retry;

public class Example0 {

  public static void main(String[] args) {

    final Flux<Integer> publisher1 = Flux.range(0, 512);
    final Flux<Integer> publisher2 = Flux.range(512, 512);

//    Flux.mergeOrdered(
//        Flux.defer(() -> {
//          System.out.println("generating data for [100]");
//          return Flux.interval(Duration.ofMillis(100)) // go actuator
//              .take(100)
//              .map(item -> "[" + 100 + "]" + item);
//        }),
//        Flux.defer(() -> {
//          System.out.println("generating data for [300]");
//          return Flux.interval(Duration.ofMillis(300)) // go actuator
//              .take(300)
//              .map(item -> "[" + 300 + "]" + item);
//        })
//    )
//        .take(101)
//        .log()
//        .blockLast();



//    final Flux<Flux<Integer>> fluxFromFluxes = Flux.just(
//        publisher1,
//        publisher2
//    );
//
//    final Flux<Integer> integerFlux = fluxFromFluxes
//        .flatMap(f -> f); // работает как merge
//
//
    Flux.range(10, 1024)
        .log("before.groupBy")
        .groupBy(i -> i % 512)
        .flatMap(fluxFromFluxes -> {
          // here you use webflux
          return fluxFromFluxes.takeLast(1);
        }, 512)
        .name("my.flux.at." + "pam")
        .metrics()
        .take(512)
        .log("asdasaffSDF", Level.INFO)
        .blockLast();

    Flux.merge(); // соединять одновременно
//    Flux.concat()

//    integerFlux // позволяет соединнять несколько источников во едино в параллели
//        .log()
//        .subscribe();

    // TODO: merge vs flatMap
    // TODO: concat vs concatMap
  }

}

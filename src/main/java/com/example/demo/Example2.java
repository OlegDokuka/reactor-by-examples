package com.example.demo;

import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.Callable;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.BiConsumer;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class Example2 {

  static final Scheduler myScheduler = Schedulers.newSingle("for-this-pipe");

  public static void main(String[] args) throws InterruptedException {

    final Scheduler single = Schedulers.single(Schedulers.parallel());

    Flux.interval(Duration.ofMillis(10), Schedulers.boundedElastic())
        .map(String::valueOf)
        .log("thread.boundary.before")
        .flatMap(__ -> { // есть шанс смены потока
          return Mono.delay(Duration.ofMillis(10 + ThreadLocalRandom.current().nextInt(100)),
              Schedulers.parallel()).thenReturn(__);
        })
        .subscribeOn(Schedulers.single())
        .map(Long::parseLong)
        .log("thread.boundary.after")
        .subscribe();

    Thread.sleep(100000);
  }

  static Callable<Integer> networkCall() {
    return () -> {
      Thread.sleep(10000);
      return 100000;
    };
  }

}

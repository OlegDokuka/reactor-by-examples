package com.example.demo;

import java.time.Duration;
import java.util.function.Function;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class ExampleBackpressure {

  static final Scheduler myScheduler = Schedulers.newSingle("for-this-pipe");

  public static void main(String[] args) throws InterruptedException {

    Flux.interval(Duration.ofMillis(1))
        .onBackpressureBuffer(10, BufferOverflowStrategy.DROP_OLDEST)
        .log("before publish on")
        .map(Function.identity())
        .map(Function.identity())
        .filter(__ -> true)
        .publishOn(Schedulers.boundedElastic(), 32)
        .doOnNext((t) -> {
          try {
            Thread.sleep(100);
          }
          catch (InterruptedException e) {
            throw new RuntimeException(e);
          }
        })
        .log()
        .map(Function.identity())
        .map(Function.identity())
        .blockLast();

  }

}

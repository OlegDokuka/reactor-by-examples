package com.example.demo;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import org.reactivestreams.Subscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;

public class Example7 {

  public static void main(String[] args) throws InterruptedException {

    // TODO: concatMap
    Flux.range(0, 100000)
        .log("here", Level.INFO, SignalType.REQUEST)
        .concatMap(i -> Mono.delay(Duration.ofMillis(100)), 0)
        .log("after")
        .subscribe(new BaseSubscriber<Object>() {
          @Override
          protected void hookOnSubscribe(Subscription subscription) {
            subscription.request(1);
            // bug
          }
        });

    Thread.sleep(100000);
  }


  static Callable<Integer> expensiveProcessing() {
    return () -> {
      Thread.sleep(10000);
      return 100000;
    };
  }

}

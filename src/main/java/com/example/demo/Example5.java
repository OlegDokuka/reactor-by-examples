package com.example.demo;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

public class Example5 {

  public static void main(String[] args) throws InterruptedException {
    // TODO publishOn
    Flux.range(0, 100000)
        .log("here", Level.INFO, SignalType.REQUEST, SignalType.ON_NEXT)
        .publishOn(Schedulers.parallel(), 1024)
        .doOnNext((t) -> {
          try {
            networkCall().call();
          } catch (Exception e) {

          }
        })
        .log("before.publish.on")
        .blockLast();
    Thread.sleep(100000);
  }

  static Callable<Integer> networkCall() {
    return () -> {
      Thread.sleep(5000);
      return 100000;
    };
  }

}

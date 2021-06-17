package com.example.demo;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

public class Example6 {

  public static void main(String[] args) throws InterruptedException {

    Flux.range(0, 1000000)
//        .log("here", Level.INFO, SignalType.REQUEST)
        .flatMap(i -> Flux.range(0, 100).log("inner"), 1, 32)
        .blockLast();
    // todo flatMap

    Thread.sleep(100000);
  }


  static Callable<Integer> networkCall() {
    return () -> {
      Thread.sleep(10000);
      return 100000;
    };
  }

}

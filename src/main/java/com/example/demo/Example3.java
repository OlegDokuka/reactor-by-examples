package com.example.demo;

import java.util.concurrent.Callable;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

public class Example3 {

  public static void main(String[] args) throws InterruptedException {

    Flux.range(0, 100000)
        .parallel()
        .runOn(Schedulers.parallel())
        .log()
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

package com.example.demo;

import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.Callable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

public class Example2 {

  public static void main(String[] args) throws InterruptedException {

    Flux.defer(() -> {
      try {
        final Integer value = networkCall().call();
        return Flux.range(0, value).startWith(value);
      } catch (Exception e) {
        return Flux.error(e);
      }
    })
        .log("thread.before.subscribeOn")
//        .subscribeOn(Schedulers.boundedElastic()) // modify signals which are coming from bottom to top (subscribe, request)
        .log("thread.after.subscribeOn")
        .log("thread.before.publishOn")
        .subscribeOn(Schedulers.parallel()) // modify any signal coming from top to bottom (onNext, onError, onComplete)
        // sometimes publishOn can modify subscripion.request
        .log("thread.after.publishOn")                                  // - Modified Thread By PublishOn
        .doOnNext((value) -> { // lets assume this is CPU intensive     // - Modified Thread By PublishOn
          try {                                                         // - Modified Thread By PublishOn
            Thread.sleep(10);                                     // - Modified Thread By PublishOn
          } catch (InterruptedException e) {                            // - Modified Thread By PublishOn
          }
        })
        .subscribe();                                                   // - Modified Thread By PublishOn

    Thread.sleep(100000);
  }

  static Callable<Integer> networkCall() {
    return () -> {
      Thread.sleep(10000);
      return 100000;
    };
  }

}

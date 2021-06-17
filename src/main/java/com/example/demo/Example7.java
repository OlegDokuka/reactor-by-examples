package com.example.demo;

import java.util.concurrent.Callable;

public class Example7 {

  public static void main(String[] args) throws InterruptedException {
//
//    // TODO: concatMap
//    final Flux<Flux<Integer>> window = Flux.range(0, 100000)
//        .log("here", Level.INFO, SignalType.REQUEST)
//        .concatMap(i -> Mono.just(i), 0)
//        .log("after")
//        .windowTimeout(100, Duration.ofMinutes(1));
//    window
//        .concatMap(inner -> inner.count())
//        .subscribe(new BaseSubscriber<Integer>() {
//          @Override
//          protected void hookOnSubscribe(Subscription subscription) {
//            subscription.request(1);
//          }
//        });



//    Mono.deferContextual((contextView) -> {
//      return Mono.just("")
//          .log("Request[xyz][session:" + contextView.get() + "]", Level.INFO, SignalType.REQUEST, )
//    })

    Thread.sleep(100000);
  }


  static Callable<Integer> expensiveProcessing() {
    return () -> {
      Thread.sleep(10000);
      return 100000;
    };
  }

}

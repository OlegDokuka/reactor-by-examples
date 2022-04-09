package com.example.demo;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.BufferOverflowStrategy;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

public class Example7 {
  static final Logger logger = LoggerFactory.getLogger(Example7.class);

  public static void main(String[] args) throws InterruptedException {

    final ExecutorService executorService = Executors.newWorkStealingPool();

    Flux.interval(Duration.ofMillis(1))
        .log("top")
        .map(Function.identity())
        .map(String::valueOf)
        .buffer(Duration.ofMillis(100))
        .take(20)
        .publishOn(Schedulers.boundedElastic(), false, 64)
        .log("bottom")
        .subscribe(new BaseSubscriber<List<String>>() {
          long receivedCnt = 0;
          long requesteCnt = 0;

          @Override
          protected void hookOnSubscribe(Subscription subscription) {
            requesteCnt = 10;
            subscription.request(32);
          }

          @Override
          protected void hookOnNext(List<String> value) {
          }

          @Override
          protected void hookFinally(SignalType type) {

          }
        });


    Thread.sleep(100000);
  }

}

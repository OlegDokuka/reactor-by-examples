package com.example.demo;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

public class Example4 {
static final Logger logger = LoggerFactory.getLogger(Example4.class);
  public static void main(String[] args) throws InterruptedException {

    // BaseSubscriber

    final ExecutorService es = Executors.newFixedThreadPool(1);

    Flux.range(0, 100000)
        .log("here", Level.INFO, SignalType.REQUEST)
        .subscribe(new BaseSubscriber<Integer>() {
          final int requestLimit = 256;

          int processedElements = 0;

          @Override
          protected void hookOnSubscribe(Subscription subscription) {
            es.submit(() -> subscription.request(requestLimit));
          }

          @Override
          protected void hookOnNext(Integer value) {
            es.submit(() -> {
              processedElements++;

              logger.info("Got value {}", value);
              if (processedElements == requestLimit) {
                request(requestLimit);
              }
            });
          }
        });
    Thread.sleep(100000);
  }

}

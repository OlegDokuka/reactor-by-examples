package com.example.demo;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

public class ExampleIntegration1 {

  static final Logger logger = LoggerFactory.getLogger(ExampleIntegration1.class);

  public static void main(String[] args) throws InterruptedException {

    // Source of data - Publisher                      -- use subscribeOn to set thread on which data are created

    // Tasks - .map, filter, doOnXXX, collect, reduce, -- use publishOn to set thread for data processing

    // Subscriber#onNext(value) {
    //
    // }

    final Mono<Integer> runnablePublisher = Mono.<Integer>fromCallable(() -> {
      try {
        Thread.sleep(123);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      logger.info("starting our pipeline"); // we wanna run this .parallel as well
      return 1;
    });
    Flux.range(0, 1024) // generate data
        .log("for.map.1", Level.INFO, SignalType.REQUEST)
        .map(i -> {   // handle
          logger.info("map1({})", i);
          return i;
        })
        .publishOn(Schedulers.parallel())
        .startWith(runnablePublisher)
        .subscribeOn(Schedulers.parallel())
        .log("for.map.2", Level.INFO, SignalType.REQUEST)
        .map(data -> {   // handle
          logger.info("map2({})", data);
          return data;
        })
        .log("after") // handle
        .blockLast(); // handle data
  }

  static Callable<Integer> networkCall() {
    return () -> {
      Thread.sleep(10000);
      return 100000;
    };
  }

}

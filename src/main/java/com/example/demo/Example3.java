package com.example.demo;

import java.util.concurrent.Callable;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;

public class Example3 {

  static final Logger logger = LoggerFactory.getLogger(Example3.class);

  public static void main(String[] args) throws InterruptedException {

    Flux.range(0, 1024) // generate data
        .log("for.map.1", Level.INFO, SignalType.REQUEST)
        .map(i -> {   // handle
          logger.info("map1({})", i);
          return i;
        })
        .subscribeOn(Schedulers.single())
        .startWith(
            Mono.<Integer>fromRunnable(() -> { // вероятней всего тут блокирующий вызов
              logger.info("starting our pipeline"); // we wanna run this .parallel as well
            })
        )
        .log("for.map.2", Level.INFO, SignalType.REQUEST)
        .subscribeOn(Schedulers.boundedElastic())

        .publishOn(Schedulers.parallel())
        .map(i -> {   // handle
          logger.info("map2({})", i);
          return i;
        })
        .publishOn(Schedulers.single())
        .log("after") // handle
        .subscribe(); // handle data

    Thread.sleep(100000);
  }

  static Callable<Integer> networkCall() {
    return () -> {
      Thread.sleep(10000);
      return 100000;
    };
  }

}

package com.example.demo;

import java.io.IOException;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;
import reactor.util.Loggers;
import reactor.util.retry.Retry;

public class Example6 {

  public static void main(String[] args) throws InterruptedException {



    Flux.range(-1, 10)
        .handle((i, sink) -> {
          try {
            sink.next(100 / i);
          }
          catch (Exception e) {
//            sink.error(new RuntimeException(e));
          }
        })
        .subscribe();

    for (int i = -1; i < 10; i++) {
      try {
        System.out.println(100 / i);
      }
      catch (Exception e) {
        // well, we ignore because we can
      }
    }










    Thread.sleep(100000);





//    Mono.<Integer>fromCallable(new Callable<Integer>() {
//          int counter = 0;
//
//          @Override
//          public Integer call() throws Exception {
//            switch (counter++) {
//              case 0:
//                throw new RuntimeException();
//              case 1:
//                return null;
//              default:
//                return counter;
//            }
//          }
//        })
//        .switchIfEmpty(
//            slowSource
////                .flatMap(value -> cache.put(key, value))
//        )
//        .retryWhen(
//            Retry.backoff(100, Duration.ofSeconds(1))
//                .filter(t -> t.getMessage().equalsIgnoreCase("boom"))
//        )
//        .onErrorResume(throwable -> {
//          if (throwable instanceof IOException) {
//            return slowSource;
//          }
//
//          return Mono.error(throwable);
//        })
//        .log()
//            .block();

    Thread.sleep(100000);
  }


  static Callable<Integer> networkCall() {
    return () -> {
      Thread.sleep(10000);
      return 100000;
    };
  }

}

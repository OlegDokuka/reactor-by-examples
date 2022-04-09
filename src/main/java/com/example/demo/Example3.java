package com.example.demo;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.ParallelFlux;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import static java.lang.System.exit;

public class Example3 {

  static final Logger logger = LoggerFactory.getLogger(Example3.class);

  public static void main(String[] args) throws InterruptedException {
/*
      final Scheduler.Worker service = Schedulers.parallel().createWorker();

      service.schedule(() -> {
          try {
              Thread.sleep(1000);
          }
          catch (InterruptedException e) {
              throw new RuntimeException(e);
          }
          System.out.println("task1");
      });

      service.schedule(() -> {
          try {
              Thread.sleep(10);
          }
          catch (InterruptedException e) {
              throw new RuntimeException(e);
          }
          System.out.println("task2");
      });

      service.schedule(() -> {
          try {
              Thread.sleep(1);
          }
          catch (InterruptedException e) {
              throw new RuntimeException(e);
          }
          System.out.println("task3");
      });

      service.schedule(() -> {
          System.out.println("task4");
      });

      service.schedule(() -> {
          System.out.println("task5");
      });

      Thread.sleep(10000);

      exit(-9);*/

      // Source of data - Publisher                      -- use subscribeOn to set thread on which data are created

    // Tasks - .map, filter, doOnXXX, collect, reduce, -- use publishOn to set thread for data processing

    // Subscriber#onNext(value) {
    //
    // }

//    final Mono<Integer> runnablePublisher = Mono.<Integer>fromCallable(() -> {
//      try {
//        Thread.sleep(123);
//      } catch (InterruptedException e) {
//        e.printStackTrace();
//      }
//      logger.info("starting our pipeline"); // we wanna run this .parallel as well
//      return 1;
//    });

//    Flux.range(0, 102400) // generate data
//        .log("for.map.1", Level.INFO, SignalType.REQUEST)
//        .map(i -> {   // handle
//            logger.info("map1({})", i);
//          return i;
//        })
//        .parallel()
//        .log("for.map.3", Level.INFO, SignalType.REQUEST)
//        .runOn(Schedulers.parallel())
//        .map(data -> {   // handle
//            try {
//                Thread.sleep(70);
//            }
//            catch (InterruptedException e) {
//                throw new RuntimeException(e);
//            }
//            logger.info("map3({})", data);
//            return data;
//        })
//        .runOn(Schedulers.parallel())
//        .log("after") // handle
//        .subscribe(); // handle data

      System.out.println("Constructing pipeline on " + Thread.currentThread());

      Flux.defer(() -> {
          // we generate data here

          System.out.println("Generating data on " + Thread.currentThread());

          final List<Integer> collect = IntStream.range(0, 1000)
                                                 .boxed()
                                                 .collect(Collectors.toList());

          // ResponseEntity<Data> response =  restTemplate.getForEntity(...)
          // return response.data()



          return Flux.fromIterable(collect);
      })
              .subscribeOn(Schedulers.boundedElastic())
              .concatMap(element -> {
                  if (element < 1) {
                      return Mono.delay(Duration.ofSeconds(1));
                  } else {
                      return Mono.never();
                  }
              })
              .takeUntilOther(Mono.delay(Duration.ofSeconds(1000)))
              .as(StepVerifier::create)
              .expectSubscription()
              .expectNoEvent(Duration.ofSeconds(1))
              .expectNext(0L)
              .expectNoEvent(Duration.ofSeconds(1))
              .expectNext(1L)
              .thenAwait(Duration.ofSeconds(1000))
              .expectComplete()
              .verify();

      System.out.println("Subscribed to pipeline");


      Thread.sleep(10000);
  }

}

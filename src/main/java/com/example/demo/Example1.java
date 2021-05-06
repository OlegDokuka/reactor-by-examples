package com.example.demo;

import java.time.Duration;
import java.util.HashMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class Example1 {

  public static void main(String[] args) {
    final HashMap<Object, Object> objectObjectHashMap = new HashMap<>();
    Flux.interval(Duration.ofMillis(10))
        // will executed in parallel. Where parallelizm == scheduler.size()
//        .log("thread.boundary.before")
        .flatMap(i -> Mono.delay(Duration.ofMillis(i % 100)).thenReturn(i))
//        .map(Long::parseLong)
        .log("thread.boundary.after")
        .blockLast();
  }

}

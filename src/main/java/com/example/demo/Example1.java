package com.example.demo;

import java.time.Duration;
import java.util.HashMap;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class Example1 {

  public static void main(String[] args) {
    Flux.interval(Duration.ofMillis(10))
        .map(String::valueOf)
        .log("thread.boundary.before")
        .map(Long::parseLong)
        .log("thread.boundary.after")
        .blockLast();
  }

}

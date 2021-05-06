package com.example.demo;

import java.util.List;
import java.util.concurrent.Callable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

public class Example8 {

//  static final ThreadLocal<String> USER_NAME_TL = new ThreadLocal<>();

  public static void main(String[] args) throws InterruptedException {
//    USER_NAME_TL.set("helloWorld");

    Mono.just(1)
        .publishOn(Schedulers.parallel())
        .flatMap(Example8::doStuff)
        .contextWrite(Context.of("username", "helloWorld"))
        .subscribe();

    Thread.sleep(100000);
  }

  public static Mono<Void> doStuff(Integer businessData) {
    return validateAccess()
        .then(Mono.fromRunnable(() -> {
            System.out.println("do stuff with data " + businessData);
        }));

  }

  public static Mono<Void> validateAccess() {
    return Mono.deferContextual(contextView -> {
      // ... validation
      if (!contextView.hasKey("username")) {
        return Mono.error(new IllegalStateException("asdas"));
      }

      final String userName = contextView.get("username");
      System.out.println("username " + userName);

      return Mono.empty();
    });
  }


}

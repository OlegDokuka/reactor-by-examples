package com.example.demo;

import java.time.Duration;
import java.util.Currency;
import java.util.function.Predicate;
import java.util.logging.Level;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class Example01 {

  public static void main(String[] args) {
    final Flux<Long> source1 = Flux.interval(Duration.ofMillis(10));
    final Flux<String> currenciesSource = Flux.interval(Duration.ofMillis(3000))
        .map(i -> "CCY_" + i);


    Flux.combineLatest((events) -> Tuples.of(events[0], events[1]), source1, currenciesSource)
        .filter(new Predicate<Tuple2>() {

          long orderId;

          @Override
          public boolean test(Tuple2 t) {
            final boolean different = !t.getT1().equals(orderId);
            orderId = (long) t.getT1();
            return different;
          }
        })
        .log()
        .blockLast();

  }

}

package com.example.demo;

import java.time.Duration;
import java.util.HashMap;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;

public class Example1 {

  public static void main(String[] args) {
    BlockingQueue<Long> values = new ArrayBlockingQueue<>(128);

    new Thread(produceValues(values)).start();
  }

  private static Runnable produceValues(BlockingQueue<Long> values) {
    return () -> {
      for (int i = 0; i < 100000; i++) {
        String is = String.valueOf(i);
        String is2 = is + "1";
        long ii = Long.parseLong(is2);
        try {
          values.put(ii);
        } catch (InterruptedException e) {
          return;
        }
      }
    };
  }
  private static Runnable consumeValues(BlockingQueue<Long> values) {
    return () -> {
      while (true) {
        try {
          final long value = values.take();
          System.out.println("Received Value [" + value + "]");
        } catch (InterruptedException e) {
          return;
        }
      }
    };
  }

}

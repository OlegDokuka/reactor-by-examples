package com.example.demo;

import java.time.Duration;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;
import reactor.core.publisher.Sinks.One;
import reactor.core.publisher.SynchronousSink;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.context.Context;

public class Example8 {

  static final ThreadLocal<String> USER_NAME_TL = new ThreadLocal<>();

  public static void main(String[] args) throws InterruptedException {
    String user = "Jon Doe";
//    USER_NAME_TL.set(user);
//
//    Schedulers.addExecutorServiceDecorator("usernameDecorator", (scheduler, scheduledExecutorService) -> new MyScheduledExecutorService(scheduledExecutorService));

    Flux.interval(Duration.ofMillis(10))
        .flatMap((__) -> {
          return validateAccess()
                .then(Mono.defer(() -> {

                  final One<Integer> one = Sinks.one();

                  new Thread(
                      () -> { // предполагаем что ето сеть / сетевой клиент (http / tcp / udc)
                        try {
                          Thread.sleep(100);
                          one.tryEmitValue(1);
                        } catch (InterruptedException e) {
                          one.tryEmitError(e);
                        }
                      }).start();

                  return one.asMono();
                }));
        })
//        .handle(new BiConsumer<Integer, SynchronousSink<? extends Integer>>() {
//          @Override
//          public void accept(Integer integer, SynchronousSink<? extends Integer> synchronousSink) {
//            final Context context = synchronousSink.currentContext();
//
//            context.hasKey("userName");
//            final Map<String, String> map = context.get("map");
//
//            map.put("asdas", "sadasd");
////            synchronousSink.next(null);
//          }
//        })
        .subscribeOn(Schedulers.boundedElastic())
        .log()
        .contextWrite(Context.of("userName", user, "map", new ConcurrentHashMap<>()))
        .subscribe();

    Thread.sleep(100000);
  }

//  public static Mono<Void> doStuff(Integer businessData) {
//    return validateAccess()
//        .then(Mono.fromRunnable(() -> {
//            System.out.println("do stuff with data " + businessData);
//        }));
//
//  }
//
//  public static Mono<Void> validateAccess() {
//    return Mono.deferContextual(contextView -> {
//      // ... validation
//      if (!contextView.hasKey("username")) {
//        return Mono.error(new IllegalStateException("asdas"));
//      }
//
//      final String userName = contextView.get("username");
//      System.out.println("username " + userName);
//
//      return Mono.empty();
//    });
//  }


  public static String doStuff(Long objectToTransform) {
    validateAccess();


    return "Hello World";
  }

  public static Mono<Void> validateAccess() {
    return Mono.deferContextual(contextView -> {
      if (contextView.hasKey("userName")) {
        final String userName = contextView.get("userName");
        if (!userName.isEmpty()) {
          // ... validation
          System.out.println("username " + userName);
          return Mono.empty();
        }
      }
      return Mono.error(new IllegalStateException("UserName is null or empty"));
    });
  }


  private static class MyScheduledExecutorService extends ScheduledThreadPoolExecutor {

    private final ScheduledExecutorService scheduledExecutorService;

    public MyScheduledExecutorService(ScheduledExecutorService scheduledExecutorService) {
      super(0);
      this.scheduledExecutorService = scheduledExecutorService;
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Callable<V> callable,
        RunnableScheduledFuture<V> task) {
      final String username = USER_NAME_TL.get();

      return new MyRunnableScheduledFuture<>(username, () -> {
        try {
          callable.call();
        } catch (Exception e) {
          e.printStackTrace();
        }
      });
    }

    @Override
    protected <V> RunnableScheduledFuture<V> decorateTask(Runnable runnable,
        RunnableScheduledFuture<V> task) {
      final String username = USER_NAME_TL.get();

      return new MyRunnableScheduledFuture<>(username, runnable);
    }

    private static class MyRunnableScheduledFuture<V> implements RunnableScheduledFuture<V> {

      private final String username;
      private final Runnable runnable;

      public MyRunnableScheduledFuture(String username, Runnable runnable) {
        this.username = username;
        this.runnable = runnable;
      }

      @Override
      public boolean cancel(boolean mayInterruptIfRunning) {
        return false;
      }

      @Override
      public boolean isCancelled() {
        return false;
      }

      @Override
      public boolean isDone() {
        return false;
      }

      @Override
      public V get() throws InterruptedException, ExecutionException {
        return null;
      }

      @Override
      public V get(long timeout, TimeUnit unit)
          throws InterruptedException, ExecutionException, TimeoutException {
        return null;
      }

      @Override
      public void run() {
        USER_NAME_TL.set(username);
        try {
          runnable.run();
        } finally {
          USER_NAME_TL.set(null);
        }
      }

      @Override
      public int compareTo(Delayed o) {
        return 0;
      }

      @Override
      public long getDelay(TimeUnit unit) {
        return 0;
      }

      @Override
      public boolean isPeriodic() {
        return false;
      }
    }
  }
}

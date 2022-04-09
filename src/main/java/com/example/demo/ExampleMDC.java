package com.example.demo;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;
import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import reactor.core.CoreSubscriber;
import reactor.core.Fuseable;
import reactor.core.Fuseable.QueueSubscription;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;
import reactor.core.publisher.Signal;
import reactor.core.scheduler.Schedulers;
import reactor.util.Loggers;
import reactor.util.context.Context;
import reactor.util.context.ContextView;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class ExampleMDC {

  static final Logger logger = LoggerFactory.getLogger(ExampleMDC.class);

  public static void main(String[] args) {
    Loggers.useSl4jLoggers();

/*    Hooks.onEachOperator("mdcContextAspecet", new Function<Publisher<Object>, Publisher<Object>>() {
      @Override
      public Publisher<Object> apply(Publisher<Object> objectPublisher) {
        final Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();

        final Function<? super Publisher<Object>, ? extends Publisher<Object>> lift =
            Operators.lift((__, downstreamSubsriber) -> { // Operators.lift - is a way to create custom operator
              return new MDCSubscriber<Object>(copyOfContextMap, downstreamSubsriber);
            });

        return lift.apply(objectPublisher);
      }
    });*/

    Hooks.addQueueWrapper("mdcContextAspect", MDCQueue::new);
    Schedulers.addExecutorServiceDecorator("mdcContextAspect", (scheduler, es) -> {
      return new MDCScheduledExecutorService(es);
    });



    MDC.put("trace", "12312-1231-321");


    final Flux<Long> my_custom_single = Flux.interval(Duration.ofMillis(10))
        // mdcSubscriberOperator Here
        .subscribeOn(Schedulers.newSingle("my custom single"))

        .doOnEach(new Consumer<Signal<Long>>() {
          final String name = "asdas";

          @Override
          public void accept(Signal<Long> signal) {
            logger.info("Hello World with signal [{}]", signal);
          }
        })
        .map(String::valueOf)
        .log("thread.boundary.before")
        .publishOn(Schedulers.single(), Integer.MAX_VALUE) // queue
        // adding context in the decorator before calling process event
        .doOnNext(ExampleMDC::processEvent)
        // adding context
        // replacing previous MDC context with the remembered
        .map(Long::parseLong)
        .log("thread.boundary.after")
        .subscribeOn(Schedulers.boundedElastic())
        .log("thread.before.subscribeOn")

        .publishOn(Schedulers.boundedElastic(), Integer.MAX_VALUE) // queue
                .doOnEach(new Consumer<Signal<Long>>() {
                  final String name = "asdas";

                  @Override
                  public void accept(Signal<Long> signal) {
                    logger.info("Hello World with signal [{}]", signal);
                  }
                })
        .contextWrite(Context.of("mdc" ,MDC.getCopyOfContextMap()));

    my_custom_single
        // mdcSubscriberOperator Here
        .blockLast();
  }

  private static void processEvent(String value) {
    final long start = System.currentTimeMillis();
    try {
      Thread.sleep(20);
    } catch (InterruptedException e) {
    }

     MDC.put("trace",
             MDC.get("trace")+ " " + value);
  }


  private static class MDCScheduledExecutorService implements ScheduledExecutorService {

    private final ScheduledExecutorService es;

    public MDCScheduledExecutorService(ScheduledExecutorService es) {
      this.es = es;
    }

    @Override
    public ScheduledFuture<?> schedule(Runnable command, long delay, TimeUnit unit) {
      final Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
      return es.schedule(() -> {
        try {
          MDC.setContextMap(copyOfContextMap);
          command.run();
        } finally {
          MDC.clear();
        }
      }, delay, unit);
    }

    @Override
    public <V> ScheduledFuture<V> schedule(Callable<V> callable, long delay, TimeUnit unit) {
      final Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
      return es.schedule(() -> {
        try {
          MDC.setContextMap(copyOfContextMap);
          return callable.call();
        } finally {
          MDC.clear();
        }
      }, delay, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleAtFixedRate(Runnable command, long initialDelay,
        long period, TimeUnit unit) {
      final Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
      return es.scheduleAtFixedRate(() -> {
        try {
          MDC.setContextMap(copyOfContextMap);
          command.run();
        } finally {
          MDC.clear();
        }
      }, initialDelay, period, unit);
    }

    @Override
    public ScheduledFuture<?> scheduleWithFixedDelay(Runnable command, long initialDelay,
        long delay, TimeUnit unit) {
      final Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
      return es.scheduleWithFixedDelay(() -> {
        try {
          MDC.setContextMap(copyOfContextMap);
          command.run();
        } finally {
          MDC.clear();
        }
      }, initialDelay, delay, unit);
    }

    @Override
    public void shutdown() {
      es.shutdown();
    }

    @Override
    public List<Runnable> shutdownNow() {
      return es.shutdownNow();
    }

    @Override
    public boolean isShutdown() {
      return es.isShutdown();
    }

    @Override
    public boolean isTerminated() {
      return es.isTerminated();
    }

    @Override
    public boolean awaitTermination(long timeout, TimeUnit unit) throws InterruptedException {
      return es.awaitTermination(timeout, unit);
    }

    @Override
    public <T> Future<T> submit(Callable<T> task) {
      final Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
      return es.submit(() -> {
        try {
          MDC.setContextMap(copyOfContextMap);
          return task.call();
        } finally {
          MDC.clear();
        }
      });
    }

    @Override
    public <T> Future<T> submit(Runnable task, T result) {
      final Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
      return es.submit(() -> {
        try {
          MDC.setContextMap(copyOfContextMap);
          task.run();
        } finally {
          MDC.clear();
        }
      }, result);
    }

    @Override
    public Future<?> submit(Runnable task) {
      final Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
      return es.submit(() -> {
        try {
          MDC.setContextMap(copyOfContextMap);
          task.run();
        } finally {
          MDC.clear();
        }
      });
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks)
        throws InterruptedException {
      return null;
    }

    @Override
    public <T> List<Future<T>> invokeAll(Collection<? extends Callable<T>> tasks, long timeout,
        TimeUnit unit) throws InterruptedException {
      return null;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks)
        throws InterruptedException, ExecutionException {
      return null;
    }

    @Override
    public <T> T invokeAny(Collection<? extends Callable<T>> tasks, long timeout, TimeUnit unit)
        throws InterruptedException, ExecutionException, TimeoutException {
      return null;
    }

    @Override
    public void execute(Runnable command) {
      final Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
      es.execute(() -> {
        try {
          MDC.setContextMap(copyOfContextMap);
          command.run();
        } finally {
          MDC.clear();
        }
      });
    }
  }

  private static class MDCQueue implements Queue<Object> {

    private final Queue<?> q;

    public MDCQueue(Queue<?> q) {
      this.q = q;
    }

    @Override
    public boolean add(Object message) {
      return ((Queue) q).add(Tuples.of(MDC.getCopyOfContextMap(), message));
    }

    @Override
    public boolean offer(Object o) {
      final Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
      return ((Queue) q).offer(
          Tuples.of(copyOfContextMap == null ? Collections.emptyMap() : copyOfContextMap, o));
    }

    @Override
    public Object remove() {
      final Tuple2<Map, Object> envelop = (Tuple2<Map, Object>) ((Queue) q).remove();
      MDC.setContextMap(envelop.getT1());
      return envelop.getT2();
    }

    @Override
    public Object poll() {
      final Tuple2<Map, Object> envelop = (Tuple2<Map, Object>) ((Queue) q).poll();
      if (envelop != null) {
        MDC.setContextMap(envelop.getT1());
        return envelop.getT2();
      }
      return envelop;
    }

    @Override
    public Object element() {
      final Tuple2<Map, Object> envelop = (Tuple2<Map, Object>) ((Queue) q).element();
      if (envelop != null) {
        MDC.setContextMap(envelop.getT1());
        return envelop.getT2();
      }
      return envelop;
    }

    @Override
    public Object peek() {
      final Tuple2<Map, Object> envelop = (Tuple2<Map, Object>) ((Queue) q).peek();
      if (envelop != null) {
        MDC.setContextMap(envelop.getT1());
        return envelop.getT2();
      }
      return envelop;
    }

    @Override
    public int size() {
      return q.size();
    }

    @Override
    public boolean isEmpty() {
      return q.isEmpty();
    }

    @Override
    public boolean contains(Object o) {
      return q.contains(o);
    }

    @Override
    public Iterator<Object> iterator() {
      return (Iterator<Object>) q.iterator();
    }

    @Override
    public Object[] toArray() {
      return Arrays.stream(q.toArray()).map(e -> ((Tuple2) e).getT2()).toArray();
    }

    @Override
    public <T> T[] toArray(T[] a) {
      return (T[]) Arrays.stream(q.toArray(a)).map(e -> ((Tuple2) e).getT2()).toArray();
    }

    @Override
    public boolean remove(Object o) {
      return false;
    }

    @Override
    public boolean containsAll(Collection<?> c) {
      return false;
    }

    @Override
    public boolean addAll(Collection<?> c) {
      return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
      return false;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
      return false;
    }

    @Override
    public void clear() {
      q.clear();
    }
  }

  private static class MDCSubscriber<T> implements CoreSubscriber<T>, QueueSubscription<T> {

    private final Map<String, String> mdcContext;
    private final CoreSubscriber<? super Object> downstream;

    private Subscription s;

    public MDCSubscriber(Map<String, String> mdcContext,
        CoreSubscriber<? super Object> downstream) {
      this.mdcContext = mdcContext;
      this.downstream = downstream;
    }

    @Override
    public void onSubscribe(Subscription s) {
      try {
        this.s = s;
        MDC.setContextMap(mdcContext);
        downstream.onSubscribe(this);
      } finally {
        MDC.clear();
      }
    }

    @Override
    public void onNext(Object o) {
      try {
        MDC.setContextMap(mdcContext);
        downstream.onNext(o);
      } finally {
        MDC.clear();
      }
    }

    @Override
    public void onError(Throwable t) {
      try {
        MDC.setContextMap(mdcContext);
        downstream.onError(t);
      } finally {
        MDC.clear();
      }
    }

    @Override
    public void onComplete() {
      try {
        MDC.setContextMap(mdcContext);
        downstream.onComplete();
      } finally {
        MDC.clear();
      }
    }

    @Override
    public void request(long n) {
      try {
        MDC.setContextMap(mdcContext);
        s.request(n);
      } finally {
        MDC.clear();
      }
    }

    @Override
    public void cancel() {
      try {
        MDC.setContextMap(mdcContext);
        s.cancel();
      } finally {
        MDC.clear();
      }
    }

    @Override
    public int requestFusion(int requestedMode) {
      try {
        MDC.setContextMap(mdcContext);
        if (s instanceof QueueSubscription) {
          return ((QueueSubscription<T>) s).requestFusion(requestedMode);
        }
        return Fuseable.NONE;
      } finally {
        MDC.clear();
      }
    }

    @Override
    public T poll() {
      return ((QueueSubscription<T>) s).poll();
    }

    @Override
    public int size() {
      return ((QueueSubscription<T>) s).size();
    }

    @Override
    public boolean isEmpty() {
      return ((QueueSubscription<T>) s).isEmpty();
    }

    @Override
    public void clear() {
      ((QueueSubscription<T>) s).clear();
    }
  }
}

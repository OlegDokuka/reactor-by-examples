import org.reactivestreams.Subscription;
import org.slf4j.MDC;
import reactor.core.CoreSubscriber;
import reactor.core.Fuseable;

import java.util.Map;

public class MDCSubscriber<T> implements CoreSubscriber<T>, Fuseable.QueueSubscription<T> {

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
        if (mdcContext != null) {
            MDC.setContextMap(mdcContext);
        }
        downstream.onSubscribe(this);
      } finally {
        MDC.clear();
      }
    }

    @Override
    public void onNext(Object o) {
      try {
        if (mdcContext != null) {
          MDC.setContextMap(mdcContext);
        }
        downstream.onNext(o);
      } finally {
        MDC.clear();
      }
    }

    @Override
    public void onError(Throwable t) {
      try {
          if (mdcContext != null) {
              MDC.setContextMap(mdcContext);
          }
        downstream.onError(t);
      } finally {
        MDC.clear();
      }
    }

    @Override
    public void onComplete() {
      try {
          if (mdcContext != null) {
              MDC.setContextMap(mdcContext);
          }
        downstream.onComplete();
      } finally {
        MDC.clear();
      }
    }

    @Override
    public void request(long n) {
      try {
      if (mdcContext != null) {
          MDC.setContextMap(mdcContext);
      }
        s.request(n);
      } finally {
        MDC.clear();
      }
    }

    @Override
    public void cancel() {
      try {
          if (mdcContext != null) {
              MDC.setContextMap(mdcContext);
          }
        s.cancel();
      } finally {
        MDC.clear();
      }
    }

    @Override
    public int requestFusion(int requestedMode) {
      try {
          if (mdcContext != null) {
              MDC.setContextMap(mdcContext);
          }
        if (s instanceof Fuseable.QueueSubscription) {
          return ((Fuseable.QueueSubscription<T>) s).requestFusion(requestedMode);
        }
        return Fuseable.NONE;
      } finally {
        MDC.clear();
      }
    }

    @Override
    public T poll() {
      return ((Fuseable.QueueSubscription<T>) s).poll();
    }

    @Override
    public int size() {
      return ((Fuseable.QueueSubscription<T>) s).size();
    }

    @Override
    public boolean isEmpty() {
      return ((Fuseable.QueueSubscription<T>) s).isEmpty();
    }

    @Override
    public void clear() {
      ((Fuseable.QueueSubscription<T>) s).clear();
    }
  }
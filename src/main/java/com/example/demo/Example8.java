package com.example.demo;

import java.time.Duration;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Delayed;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.RunnableScheduledFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import reactor.blockhound.BlockHound;
import reactor.blockhound.BlockHound.Builder;
import reactor.blockhound.integration.BlockHoundIntegration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.ReactorBlockHoundIntegration;
import reactor.core.scheduler.Schedulers;
import reactor.tools.agent.ReactorDebugAgent;
import reactor.util.context.Context;
import reactor.util.context.ContextView;

public class Example8 {

	static final ThreadLocal<String> USER_NAME_TL = new InheritableThreadLocal<>();

	static {
		Schedulers.parallel()
		          .start();
		Schedulers.parallel()
		          .schedule(() -> {

		          });
	}

	public static void main(String[] args) throws InterruptedException {
		String user = "Jon Doe";

		Flux.interval(Duration.ofSeconds(1))
		    .flatMap(value -> {
              USER_NAME_TL.set(user + value);
              System.out.println(USER_NAME_TL.get());
              return doStuff(value)
                  .contextWrite(previous -> previous.put("username", user + value))
                  .subscribeOn(Schedulers.boundedElastic());
            })
		    .subscribe();

		Thread.sleep(10000);
	}

	/*public static String doStuff(Long objectToTransform) {
		validateAccess();

		return "Hello World";
	}

	public static void validateAccess() {
		final String userName = USER_NAME_TL.get();
		if (userName != null && !userName.isEmpty()) {
			// ... validation
			System.out.println("username " + userName);
			return;
		}

		throw new IllegalStateException("UserName is null or empty");
	}*/



  public static Mono<Void> doStuff(Long businessData) {
    return validateAccess()
        .then(Mono.fromRunnable(() -> {
            System.out.println("do stuff with data " + businessData);
        }));
  }

  public static Mono<Void> validateAccess() {
    return Mono.<Void>deferContextual(contextView -> {
      if (!contextView.hasKey("username")) {
        return Mono.error(new IllegalAccessError("boom"));
      }

      final String userName = contextView.get("username");
      System.out.println("username " + userName);

      return Mono.empty();
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
				}
				catch (Exception e) {
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

		private static class MyRunnableScheduledFuture<V>
				implements RunnableScheduledFuture<V> {

			private final String   username;
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
				}
				finally {
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

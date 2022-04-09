package com.example.demo;

import java.time.Duration;
import java.util.concurrent.ThreadFactory;
import java.util.function.Function;
import java.util.logging.Level;

import reactor.blockhound.BlockHound;
import reactor.blockhound.integration.BlockHoundIntegration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.tools.agent.ReactorDebugAgent;

public class ExampleDebugging {

	static {
		ReactorDebugAgent.init();
		ReactorDebugAgent.processExistingClasses();
		BlockHound.install(new BlockHoundIntegration() {
			@Override
			public void applyTo(BlockHound.Builder builder) {
				builder.nonBlockingThreadPredicate(previousPredicate ->
						previousPredicate.or(thread -> thread.getName().contains("non" +
								"-blocking"))
				);
			}
		});
	}

	public static void main(String[] args) throws InterruptedException {
		/*Schedulers.setFactory(new Schedulers.Factory() {
			@Override
			public Scheduler newElastic(int ttlSeconds, ThreadFactory threadFactory) {
				return immediate;
			}

			@Override
			public Scheduler newBoundedElastic(int threadCap,
					int queuedTaskCap,
					ThreadFactory threadFactory,
					int ttlSeconds) {
				return immediate;
			}

			@Override
			public Scheduler newParallel(int parallelism, ThreadFactory threadFactory) {
				return immediate;
			}

			@Override
			public Scheduler newSingle(ThreadFactory threadFactory) {
				return immediate;
			}
		});*/


		String user = "Jon Doe";

		Flux.range(0, 100)
			.log()
			.map(v -> {
				// doing stuff
				final String s = v.toString();
				return s;
			})
			.tag("place", "before publishOn")
			.name("my pipe")
			.metrics()
			.publishOn(Schedulers.newBoundedElastic(5, 1000, "non-blocking"))
			.filter(__ -> true)

			.map(v -> {
				// doing stuff
				try {
					Thread.sleep(100);
				}
				catch (InterruptedException e) {
				}

				final long l = Long.parseLong(v);
				return l;
			})
			.log("before", Level.INFO, SignalType.ON_NEXT)
		    .flatMap(value ->
				    doStuff(value)
//						    .contextWrite(previous -> previous.put("username", user + value))
						    .subscribeOn(Schedulers.boundedElastic())
		    )

			.map(Function.identity())
		    .log("after")
			.tag("place", "after publishOn")
			.name("my pipe")
		    .metrics()
		    .subscribe();

		Thread.sleep(100000);
	}

	public static Mono<Void> doStuff(long businessData) {
		return validateAccess().then(Mono.fromRunnable(() -> {
//			System.out.println("do stuff with data " + businessData);
		}));
	}


	static Mono<?> throwableMono = Mono.error(new IllegalAccessError("boom"));

	public static Mono<Void> validateAccess() {
		return Mono.<Void>deferContextual(contextView -> {
			if (!contextView.hasKey("username")) {
				return (Mono<? extends Void>) throwableMono;
			}

			final String userName = contextView.get("username");
//			System.out.println("username " + userName);

			return Mono.empty();
		});
	}

}

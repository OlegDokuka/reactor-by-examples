import io.micrometer.context.ContextRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Scannable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Hooks;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Operators;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.Map;

public class Problem {
    static final Logger logger =
            LoggerFactory.getLogger(Problem.class);

    static final WebClient client = WebClient.builder().build();

    public static void main(String[] mainArgs) {
//        Schedulers.onScheduleHook("threadLocal_propagator", (runnable) -> {
//            Map<String, String> copyOfContextMap = MDC.getCopyOfContextMap();
//            return () -> {
//                Map<String, String> oldCTXMap = MDC.getCopyOfContextMap();
//                if (copyOfContextMap != null) {
//                    MDC.setContextMap(copyOfContextMap);
//                }
//                try {
//                    runnable.run();
//                } finally {
//                    if (oldCTXMap != null) {
//                        MDC.setContextMap(oldCTXMap);
//                    }
//                }
//            };
//        });
//        Schedulers.resetFactory();


//
        Hooks.onEachOperator(
                "MDC",
                Operators.lift((scannable, downstream) -> {
                    if (scannable.scan(Scannable.Attr.PREFETCH) > 0) {
                        return new MDCSubscriber<>(MDC.getCopyOfContextMap(), downstream);
                    }
                    return downstream;
                })
       );
//
//        Hooks.onLastOperator("MDC", Operators.lift((scannable, downstream) -> {
//            if (scannable.scan(Scannable.Attr.PREFETCH) > 0) {
//                return new MDCSubscriber<>(MDC.getCopyOfContextMap(), downstream);
//            }
//            return downstream;
//        }));


        ContextRegistry contextRegistry = ContextRegistry.getInstance();
        contextRegistry.registerThreadLocalAccessor("MDC12341234",
                MDC::getCopyOfContextMap,
                MDC::setContextMap,
                MDC::clear
        );

        Hooks.enableAutomaticContextPropagation();
//        Hooks.onLastOperator(publisher -> {
//            if (publisher instanceof Mono) {
//                return ((Mono<Object>) publisher).contextCapture();
//            } else {
//                return Flux.from(publisher).contextCapture();
//            }
//        });


        Flux.interval(Duration.ofMillis(1))
            .onBackpressureDrop()
            .map(id -> "User{" + id + "}")
            .flatMap(user -> {
                MDC.put("user", user);
                logger.info("before call");
                return call()
                        .doOnNext(t -> logger.info("Actual user is " + user))
                        .map(t -> Tuples.of(user, t))
                        .<Tuple2<String, Tuple2<String, String>>>handle((t, sink) -> {
                            logger.info("Actual user inside handle is " + user);
                            sink.next(t);
                        })
                        .contextCapture()
                        .doOnNext((a) -> logger.info("No context for user " + user));
            })
            .doOnNext(t -> logger.info("After flatMap actual user is " + t.getT1()))
            .blockLast();
    }


    static Mono<Tuple2<String, String>> call() {
        return Mono.zip(
                    client.get()
                          .uri("https://swapi.dev/api/people/1")
                          .retrieve()
                          .bodyToMono(String.class),
                    client.get()
                          .uri("https://httpbin.org/anything")
                          .retrieve()
                          .bodyToMono(String.class)
                          .onErrorResume(t -> Mono.delay(Duration.ofMillis(100), Schedulers.single()).then(Mono.empty()))
                )
                .doOnNext(__ -> logger.info("response"));
    }
//
//    static Mono<Tuple2<String, String>> call() {
//        return Mono.zip(
//                  Mono.delay(Duration.ofMillis(100)).map(Object::toString),
//                    Mono.delay(Duration.ofMillis(100), Schedulers.single()).map(Object::toString)
//                )
//                .doOnNext(__ -> logger.info("response"));
//    }
}

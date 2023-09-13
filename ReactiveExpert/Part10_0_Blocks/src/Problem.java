import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.Disposable;
import reactor.core.Scannable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.time.Duration;
import java.util.*;

public class Problem {
    static final Logger logger =
            LoggerFactory.getLogger(Problem.class);


    static final ThreadLocal<String> USER_NAME_TL = new InheritableThreadLocal<>();
    static final WebClient client = WebClient.builder().build();
    private static Map<String, Scannable.Attr<?>> attrList  = Map.ofEntries(
            new AbstractMap.SimpleEntry<>("PARENT", Scannable.Attr.PARENT),
            new AbstractMap.SimpleEntry<>("TERMINATED", Scannable.Attr.TERMINATED),
            new AbstractMap.SimpleEntry<>("CANCELLED", Scannable.Attr.CANCELLED),
            new AbstractMap.SimpleEntry<>("BUFFERED", Scannable.Attr.BUFFERED),
            new AbstractMap.SimpleEntry<>("CAPACITY", Scannable.Attr.CAPACITY),
            new AbstractMap.SimpleEntry<>("PREFETCH", Scannable.Attr.PREFETCH),
            new AbstractMap.SimpleEntry<>("ACTUAL", Scannable.Attr.ACTUAL),
            new AbstractMap.SimpleEntry<>("DELAY_ERROR", Scannable.Attr.DELAY_ERROR),
            new AbstractMap.SimpleEntry<>("LIFTER", Scannable.Attr.LIFTER),
            new AbstractMap.SimpleEntry<>("NAME", Scannable.Attr.NAME),
            new AbstractMap.SimpleEntry<>("REQUESTED_FROM_DOWNSTREAM", Scannable.Attr.REQUESTED_FROM_DOWNSTREAM),
            new AbstractMap.SimpleEntry<>("RUN_ON", Scannable.Attr.RUN_ON),
            new AbstractMap.SimpleEntry<>("RUN_STYLE", Scannable.Attr.RUN_STYLE)
    );

    public static void main(String[] mainArgs) throws InterruptedException {
        Disposable subscribe = Flux.interval(Duration.ofSeconds(5))
                .map(id -> "User{" + id + "}")
                .filter(p -> true)
                .flatMap(user -> {
                    USER_NAME_TL.set(user);
                    return call().doOnNext(t -> logger.info("Requester user is " + user + " response containing user is " + t.getT1()));
                })
                .handle((s, si) -> si.next(s))
                .transform(f -> {
                    return f;
                })
                .limitRate(32)
                .subscribe();


        Flux.interval(Duration.ofSeconds(1))
                .subscribe(__ -> scanner(subscribe));

        Thread.sleep(100000);
    }


    static void scanner(Object o) {
        Scannable from = Scannable.from(o);

        System.out.println("Scanning operator : " + from);
        for (Map.Entry<String, Scannable.Attr<?>> attr : attrList.entrySet()) {
            System.out.println(attr.getKey() + ": " + from.scan(attr.getValue()));
        }

        System.out.println("------------");

        for (Scannable scannable : from.parents().toList()) {
            System.out.println("Scanning operator : " + scannable);
            for (Map.Entry<String, Scannable.Attr<?>> attr : attrList.entrySet()) {
                System.out.println(attr.getKey() + ": " + scannable.scan(attr.getValue()));
            }
            List<? extends Scannable> inners = scannable.inners().toList();
            System.out.println("INNERS : " + inners);
                System.out.println("ACTUALS : " + scannable.actuals().toList());

                inners.forEach(Problem::scanner);

            System.out.println("------------");
        }
    }


    static Mono<Tuple3<String, String, String>> call() {
        return Mono.zip(
                    client.get()
                            .uri("https://stackoverflow.com")
                            .retrieve()
                            .bodyToMono(String.class),
                    client.get()
                            .uri("https://httpbin.org/anything")
                            .retrieve()
                            .bodyToMono(String.class)
                            .onErrorResume(t -> Mono.delay(Duration.ofMillis(100), Schedulers.single()).then(Mono.empty()))
                )
                .map(response -> Tuples.of(USER_NAME_TL.get(), response.getT1(), response.getT2()));
    }
}

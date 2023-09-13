import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.time.Duration;

public class Problem {
    static final Logger logger =
            LoggerFactory.getLogger(Problem.class);


    static final ThreadLocal<String> USER_NAME_TL = new InheritableThreadLocal<>();
    static final WebClient client = WebClient.builder().build();

    public static void main(String[] mainArgs) {
        USER_NAME_TL.set("user_name");
        Flux.interval(Duration.ofSeconds(5))
            .map(id -> "User{" + id + "}")
            .flatMap(user -> {
                USER_NAME_TL.set(user);
                return call()
                        .doOnNext(t -> logger.info("Requester user is " + user + " response containing user is " + t.getT1()))
                        .doFinally(__ -> USER_NAME_TL.remove());
            })
            .blockLast();

//        Tuple3<String, String, String> x = blockingCall();
//        System.out.println(x.getT1());
    }

    static Tuple3<String, String, String> blockingCall() {
        Tuple2<String, String> response = Mono.zip(
                client.get()
                        .uri("https://stackoverflow.com")
                        .retrieve()
                        .bodyToMono(String.class),
                client.get()
                        .uri("https://httpbin.org/anything")
                        .retrieve()
                        .bodyToMono(String.class)
                        .onErrorResume(t -> Mono.delay(Duration.ofMillis(100), Schedulers.single()).then(Mono.empty()))
        ).block();

        return Tuples.of(USER_NAME_TL.get(), response.getT1(), response.getT2());
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

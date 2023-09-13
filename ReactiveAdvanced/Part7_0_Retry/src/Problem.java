import java.time.Duration;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.SignalType;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;
import reactor.util.retry.Retry;

public class Problem {

    public static void main(String[] args) throws InterruptedException {
        Flux.error(new Throwable())
            .log()
                .retry()
//            .retryWhen(
//                Retry.backoff(10, Duration.ofMillis(100))
//                     .maxBackoff(Duration.ofSeconds(10))
//            )
            .blockLast();
    }
}

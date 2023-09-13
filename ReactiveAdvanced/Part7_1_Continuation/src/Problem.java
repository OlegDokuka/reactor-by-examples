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
        var constantValue = 10;
//        try { // onErrorResume in imperative
//            for (int i = 0; i < 100; i++) {
//                    System.out.println(constantValue / i);
//
//            }
//        } catch (Exception e) {
//            // that is fine
//        }

        for (int i = 0; i < 100; i++) {
            try { // onErrorContinue in imperative
                System.out.println(constantValue / i);
            } catch (Exception e) {
                // that is fine
            }

        }
    }
}

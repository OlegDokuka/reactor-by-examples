import java.time.Duration;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

public class Problem {


	public static void main(String[] args) throws InterruptedException {
		ScheduledExecutorService service = Executors.newScheduledThreadPool(5);

		for (int i = 0; i < 5; i++) {
			var constI = i;
			service.submit(() -> System.out.println("Hello world " + constI));
		}

		Thread.sleep(1000);
	}

}

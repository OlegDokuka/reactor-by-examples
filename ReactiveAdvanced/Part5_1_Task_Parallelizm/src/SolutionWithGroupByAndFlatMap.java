import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple2;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Flow;
import java.util.stream.Collectors;

public class SolutionWithGroupByAndFlatMap {

	// 1       A
	// 2       |
	// 3       B
	// 4       |
	// 5       D
	//

	public static void main(String[] args) throws InterruptedException {

		Sinks.Many<Object> a = Sinks.many().multicast().directBestEffort();
		Sinks.Many<Object> b = Sinks.many().multicast().directBestEffort();

		long start = System.nanoTime();
		Flux.range(0, 100)
			.delayElements(Duration.ofMillis(10), Schedulers.single())
			.doOnNext((value) -> {
//				System.out.println(Thread.currentThread() + " Doing Processing 1 " + value);
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					throw new RuntimeException(e);
				}
			})
				.groupBy(event -> event % 2)
				.flatMap(group ->
					group.publishOn(Schedulers.parallel())
						 .doOnNext((value) -> {
//							System.out.println(Thread.currentThread() + " Doing Processing 2 " + value);
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
								throw new RuntimeException(e);
							}
						 })
//							.transform(writeToDB)
//							.then()
						 .log(group.key() == 0 ? "Even" : "Odd")
						, 5) // async boundary - new worker responsible for processing of the rest of the tasks

				.then()
				.block();

		long end = System.nanoTime();

		System.out.println("Execution time is " + (end - start) / 1_000_000d );
	}
//
//	class Node {
//
//		List<ChildNode> childs = new ArrayList<>();
//		Function combiner = ...
//		Flux<Object> stream() {
//			return combiner.apply(childs.stream().map(Node::stream).collect(Collectors.toList()))
//		}
//	}
//
//	// queue((a + b) + c) * (d / 2)
//
//	class ChildNode extends Node {
//
//	}

}

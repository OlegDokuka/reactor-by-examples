package org.test.reactive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import reactor.core.publisher.Flux;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 2, time = 5)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 1)
@State(Scope.Thread)
public class FMapVsCMapPerfTest {

  @Param({"10000"})
  public int times;

  Flux<Integer> source;

  List<Integer> data;

  @Setup
  public void setup() {
    final Integer[] a = new Integer[1000];
    Arrays.fill(a, 1);
    data = Arrays.asList(a);
    source = Flux.range(0, times).hide();
  }

  @Benchmark
  public void flatMap() {
    source
        .flatMap(i -> Flux.fromIterable(data))
        .map(Object::toString)
        .filter(__ -> true)
        .map(Object::toString)
        .filter(__ -> true)
        .blockLast();
  }

  @Benchmark
  public void concatMap() {
    source
        .concatMap(i -> Flux.fromIterable(data))
        .map(Object::toString)
        .filter(__ -> true)
        .map(Object::toString)
        .filter(__ -> true)
        .blockLast();
  }

  @Benchmark
  public void concatMapIterable() {
    source
        .concatMapIterable(i -> data)
                .map(Object::toString)
                .filter(__ -> true)
                .map(Object::toString)
                .filter(__ -> true)

        .blockLast();
  }
}
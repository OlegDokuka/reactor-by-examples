package org.test.reactive;

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
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 1)
@State(Scope.Thread)
public class FMapVsCMapPerfTest {
    @Param({ "10000" })
    public int times;

    Flux<Integer> source;

    @Setup
    public void setup() {
        source = Flux.range(0, times).hide();
    }

    @Benchmark
    public void flatMap() {
        source
            .flatMap(i -> Flux.fromStream(IntStream.range(i, i + 512).boxed()))
            .blockLast();
    }

    @Benchmark
    public void concatMap() {
        source
            .concatMapIterable(i -> IntStream.range(i, i + 512).boxed().collect(Collectors.toList()))
            .blockLast();
    }
}
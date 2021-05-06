package org.test.reactive;

import java.io.IOException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.RunnerException;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

@BenchmarkMode(Mode.Throughput)
@Warmup(iterations = 2, time = 5)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@OutputTimeUnit(TimeUnit.SECONDS)
@Fork(value = 1)
@State(Scope.Thread)
public class PublishOnPerfTest {
    @Param({ "1000000" })
    public int times;

    Flux<Object> source;

    @Setup
    public void setup() {
        source = Flux.range(0, times).map(__ -> new Object());
    }

    @Benchmark
    public void measure256() {
        source
            .publishOn(Schedulers.parallel(), 256)
            .blockLast();
    }

    @Benchmark
    public void measure64() {
        source
            .publishOn(Schedulers.parallel(), 64)
            .blockLast();
    }
}
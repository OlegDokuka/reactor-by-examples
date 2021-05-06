package org.test.reactive;

import java.util.ArrayList;
import java.util.Collection;
import org.openjdk.jmh.infra.BenchmarkParams;
import org.openjdk.jmh.infra.IterationParams;
import org.openjdk.jmh.profile.InternalProfiler;
import org.openjdk.jmh.results.AggregationPolicy;
import org.openjdk.jmh.results.IterationResult;
import org.openjdk.jmh.results.Result;
import org.openjdk.jmh.results.ScalarResult;

public class MaxMemoryProfiler implements InternalProfiler {

    @Override
    public String getDescription() {
        return "Max memory heap profiler";
    }

    @Override
    public void beforeIteration(BenchmarkParams benchmarkParams, IterationParams iterationParams) {

    }

    @Override
    public Collection<? extends Result> afterIteration(BenchmarkParams benchmarkParams, IterationParams iterationParams,
        IterationResult result) {

        long totalHeap = Runtime.getRuntime().totalMemory(); // Here the value
                                                         // you want to
                                                         // collect

        Collection<ScalarResult> results = new ArrayList<>();
        results.add(new ScalarResult("Max memory heap", totalHeap, "bytes", AggregationPolicy.MAX));

        return results;
    }
}
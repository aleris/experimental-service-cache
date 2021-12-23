package ro.adriantosca.experimentalservicecache.cache;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 1)
@Measurement(iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS)
public class Benchmarks {
    private final AdiMemoryCache<String, String> cache1 = new AdiMemoryCache.Builder<String, String>().withMaximumSize(10).build();
    private final Adi2MemoryCache<String, String> cache2 = new Adi2MemoryCache.Builder<String, String>().withMaximumSize(10).build();
    private final ExternalGet<String, String> externalGet = "value for %s"::formatted;

    @Benchmark
    @Threads(10)
    public String test__1() {
        return cache1.get("id%s".formatted(ThreadLocalRandom.current().nextInt(15)), externalGet);
    }

    @Benchmark
    @Threads(10)
    public String test__2() {
        return cache2.get("id%s".formatted(ThreadLocalRandom.current().nextInt(15)), externalGet);
    }
}

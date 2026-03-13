package com.github.tnewman.concurrentslidingwindow;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Group;
import org.openjdk.jmh.annotations.GroupThreads;
import org.openjdk.jmh.annotations.Level;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Threads;
import org.openjdk.jmh.annotations.Warmup;

@State(Scope.Benchmark)
@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 3, time = 1)
@Measurement(iterations = 5, time = 1)
@Fork(1)
@Threads(Threads.MAX)
public class SlidingWindowCounterBenchmark {

  private static final int WINDOW_SIZE = 64;

  private final SlidingWindowCounter counter = new SlidingWindowCounter(WINDOW_SIZE);

  @Benchmark
  public void benchmarkAdd() {
    counter.add(1);
  }

  @Benchmark
  public long benchmarkSum() {
    return counter.getAccumulator();
  }

  @Benchmark
  public void benchmarkAdvance() {
    counter.advance();
  }

  @State(Scope.Group)
  public static class CombinedState {
    final SlidingWindowCounter counter = new SlidingWindowCounter(WINDOW_SIZE);
    private ScheduledExecutorService scheduler;

    @Setup(Level.Iteration)
    public void setup() {
      scheduler = Executors.newSingleThreadScheduledExecutor();
      scheduler.scheduleAtFixedRate(counter::advance, 1, 1, TimeUnit.SECONDS);
    }

    @TearDown(Level.Iteration)
    public void tearDown() {
      if (scheduler != null) {
        scheduler.shutdownNow();
      }
    }
  }

  @Benchmark
  @Group("combined")
  @GroupThreads(4)
  public void combinedAdd(CombinedState state) {
    state.counter.add(1);
  }

  @Benchmark
  @Group("combined")
  @GroupThreads(1)
  public long combinedSum(CombinedState state) {
    return state.counter.getAccumulator();
  }
}

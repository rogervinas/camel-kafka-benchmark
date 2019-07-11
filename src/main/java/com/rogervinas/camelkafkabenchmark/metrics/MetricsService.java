package com.rogervinas.camelkafkabenchmark.metrics;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import static java.lang.String.format;
import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;

public class MetricsService {

  private long startTime = currentTimeMillis();
  private Map<Thread, ThreadMetrics> metrics = new HashMap<>();

  public void reset() {
    startTime = currentTimeMillis();
    metrics.clear();
  }

  public void inc() {
    metrics.compute(currentThread(), (t,tm) -> tm == null ? new ThreadMetrics(1) : tm.inc());
  }

  public Metrics result() {
    final int totalCount = metrics.values().stream()
        .map(m -> m.count)
        .reduce(0, (c1,c2) -> c1+c2);

    final long lastTime = metrics.values().stream()
        .map(m -> m.time)
        .max(Comparator.comparing(Long::valueOf))
        .orElse(startTime);

    final long threadCount = metrics.keySet().stream()
        .map(thread -> thread.getName())
        .distinct().count();

    final double[] counts = metrics.values().stream().mapToDouble(m -> m.count).toArray();

    return new Metrics(threadCount, totalCount, new Mean().evaluate(counts), new StandardDeviation().evaluate(counts), lastTime - startTime);
  }

  public static class Metrics {
    public final long threadCount;
    public final int totalCount;
    public final double mean;
    public final double stddev;
    public final long duration;

    public Metrics(long threadCount, int totalCount, double mean, double stddev, long duration) {
      this.threadCount = threadCount;
      this.totalCount = totalCount;
      this.mean = mean;
      this.stddev = stddev;
      this.duration = duration;
    }

    public String toString() {
      return format(
          "%d threads | %d total | %f mean | %f stddev | %d millis",
          threadCount,
          totalCount,
          mean,
          stddev,
          duration
      );
    }
  }

  private static class ThreadMetrics {
    private final int count;
    private final long time = currentTimeMillis();

    public ThreadMetrics(int count) {
      this.count = count;
    }

    public ThreadMetrics inc() {
      return new ThreadMetrics(count + 1);
    }
  }
}

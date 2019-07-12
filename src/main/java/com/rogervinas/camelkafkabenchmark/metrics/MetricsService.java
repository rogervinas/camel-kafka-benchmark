package com.rogervinas.camelkafkabenchmark.metrics;

import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.stat.descriptive.moment.Mean;
import org.apache.commons.math3.stat.descriptive.moment.StandardDeviation;

import static java.lang.System.currentTimeMillis;
import static java.lang.Thread.currentThread;

public class MetricsService {

  private final int partitions;
  private final int consumers;

  private long startTime = currentTimeMillis();
  private final Map<Thread, ThreadMetrics> metrics = new HashMap<>();

  public MetricsService(int partitions, int consumers) {
    this.partitions = partitions;
    this.consumers = consumers;
  }

  public void reset() {
    this.startTime = currentTimeMillis();
    this.metrics.clear();
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

    return new Metrics(partitions, consumers, threadCount, totalCount, new Mean().evaluate(counts), new StandardDeviation().evaluate(counts), lastTime - startTime);
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

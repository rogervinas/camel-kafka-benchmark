package com.rogervinas.camelkafkabenchmark.metrics;

public class Metrics {
  public final int partitions;
  public final int consumers;
  public final long threads;
  public final int messages;
  public final double mean;
  public final double stddev;
  public final long duration;

  public Metrics(int partitions, int consumers, long threads, int messages, double mean, double stddev, long duration) {
    this.partitions = partitions;
    this.consumers = consumers;
    this.threads = threads;
    this.messages = messages;
    this.mean = mean;
    this.stddev = stddev;
    this.duration = duration;
  }
}

package com.rogervinas.camelkafkabenchmark;

import com.rogervinas.camelkafkabenchmark.kafka.KafkaRouteBenchmark;
import com.rogervinas.camelkafkabenchmark.metrics.Metrics;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import static java.lang.String.format;
import static java.util.Arrays.asList;

@ShellComponent
public class ApplicationShell {

  private static final int[] PARTITIONS = {5, 10, 20, 40};
  private static final int[] CONSUMERS = {1, 5, 10, 20, 40};

  @ShellMethod("benchmark")
  public String benchmark(
      @ShellOption int partitions,
      @ShellOption int consumers,
      @ShellOption(defaultValue = "20") int executionSeconds,
      @ShellOption(defaultValue = "10000") int numberOfMessages
  ) throws Exception {
    Metrics result = execute(partitions, consumers, executionSeconds, numberOfMessages);
    return print(asList(result));
  }

  @ShellMethod("benchmark-all")
  public String benchmarkAll(
      @ShellOption(defaultValue = "20") int executionSeconds,
      @ShellOption(defaultValue = "10000") int numberOfMessages
  ) throws Exception {
    List<Metrics> results = new ArrayList<>();
    for (int partitions : PARTITIONS) {
      for (int consumers : CONSUMERS) {
        results.add(execute(partitions, consumers, executionSeconds, numberOfMessages));
      }
    }
    return print(results);
  }

  private Metrics execute(
      int partitions,
      int consumers,
      int executionSeconds,
      int numberOfMessages
  ) throws Exception {
    KafkaRouteBenchmark test = new KafkaRouteBenchmark(partitions, consumers)
        .withExecutionDuration(Duration.ofSeconds(executionSeconds))
        .withNumberOfMessages(numberOfMessages);
    test.setUp();
    Metrics result = test.execute();
    test.tearDown();
    return result;
  }

  private String print(List<Metrics> results) {
    final StringBuffer text = new StringBuffer();
    text.append("partitions;consumers;threads;message;mean;stddev;duration\n");
    results.forEach(r -> text.append(format("%d;%d;%d;%d;%f;%f;%d\n", r.partitions, r.consumers, r.threads, r.messages, r.mean, r.stddev, r.duration)));
    return text.toString();
  }
}

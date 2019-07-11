package com.rogervinas.camelkafkabenchmark;

import com.rogervinas.camelkafkabenchmark.kafka.KafkaRouteBenchmark;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;

import static java.lang.String.format;

@ShellComponent
public class ApplicationShell {

  private static final int[] PARTITIONS = {5, 10, 20, 40};
  private static final int[] CONSUMERS = {1, 5, 10, 20, 40};

  @ShellMethod("benchmark")
  public String benchmark(
      @ShellOption int partitions,
      @ShellOption int consumers
  ) throws Exception {
    KafkaRouteBenchmark test = new KafkaRouteBenchmark(partitions, consumers, consumers);
    test.setUp();
    final String result = test.execute();
    test.tearDown();
    return result;
  }

  @ShellMethod("benchmark-all")
  public String benchmarkAll() throws Exception {
    String resultTextAll = "";
    for (int partitions : PARTITIONS) {
      for (int consumers : CONSUMERS) {
        String result = benchmark(partitions, consumers);
        resultTextAll += format("%d partitions | %d consumers | %s\n", partitions, consumers, result);
      }
    }
    return resultTextAll;
  }
}

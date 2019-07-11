package com.rogervinas.camelkafkabenchmark;

import com.rogervinas.camelkafkabenchmark.routes.KafkaRouteTest;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class ApplicationShell {

  @ShellMethod("route-start")
  public void benchmark(String topic, int consumersCount, int consumerStreams) throws Exception {
    KafkaRouteTest test = new KafkaRouteTest(topic, consumersCount, consumerStreams);
    test.setUp();
    test.execute();
    test.tearDown();
  }
}

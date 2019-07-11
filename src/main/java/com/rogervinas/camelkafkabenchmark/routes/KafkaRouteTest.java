package com.rogervinas.camelkafkabenchmark.routes;

import org.apache.camel.RoutesBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;

public class KafkaRouteTest extends CamelTestSupport {

  private static final String HOST = "localhost:9092";
  private static final String GROUP_ID = "test";

  private final String topic;
  private final int consumersCount;
  private final int consumerStreams;

  public KafkaRouteTest(String topic, int consumersCount, int consumerStreams) {
    this.topic = topic;
    this.consumersCount = consumersCount;
    this.consumerStreams = consumerStreams;
  }

  @Override
  protected RoutesBuilder createRouteBuilder() {
    return new KafkaRouteBuilder(HOST, topic, GROUP_ID, consumersCount, consumerStreams);
  }

  public void execute() {
  }
}

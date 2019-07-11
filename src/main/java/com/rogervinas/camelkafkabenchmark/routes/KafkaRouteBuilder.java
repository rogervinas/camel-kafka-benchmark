package com.rogervinas.camelkafkabenchmark.routes;

import org.apache.camel.builder.RouteBuilder;

public class KafkaRouteBuilder extends RouteBuilder {

  private final String host;
  private final String topic;
  private final String groupId;
  private final int consumersCount;
  private final int consumerStreams;

  public KafkaRouteBuilder(
      String host,
      String topic,
      String groupId,
      int consumersCount,
      int consumerStreams
  ) {
    this.topic = topic;
    this.host = host;
    this.groupId = groupId;
    this.consumersCount = consumersCount;
    this.consumerStreams = consumerStreams;
  }

  @Override
  public void configure() throws Exception {
    from(
        "kafka:" + topic + "?brokers=" + host
        + "&groupId=" + groupId
        + "&consumersCount=" + consumersCount
        + "&consumerStreams=" + consumerStreams
    )
        .log("${body}");
  }
}

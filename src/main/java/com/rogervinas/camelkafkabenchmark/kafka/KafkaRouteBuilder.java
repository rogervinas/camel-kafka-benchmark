package com.rogervinas.camelkafkabenchmark.kafka;

import com.rogervinas.camelkafkabenchmark.metrics.MetricsService;
import java.io.File;
import java.io.FileWriter;
import java.io.Writer;
import org.apache.camel.builder.RouteBuilder;
import org.apache.commons.io.IOUtils;

public class KafkaRouteBuilder extends RouteBuilder {

  private final String routeId;
  private final String host;
  private final String topic;
  private final String groupId;
  private final int consumersCount;
  private final int consumerStreams;
  private final MetricsService metricsService;

  public KafkaRouteBuilder(
      String routeId,
      String host,
      String topic,
      String groupId,
      int consumersCount,
      int consumerStreams,
      MetricsService metricsService
  ) {
    this.routeId = routeId;
    this.host = host;
    this.topic = topic;
    this.groupId = groupId;
    this.consumersCount = consumersCount;
    this.consumerStreams = consumerStreams;
    this.metricsService = metricsService;
  }

  @Override
  public void configure() throws Exception {
    from(
        "kafka:" + topic + "?brokers=" + host
        + "&groupId=" + groupId
        + "&consumersCount=" + consumersCount
        + "&consumerStreams=" + consumerStreams
        + "&autoOffsetReset=earliest"
    )
        .routeId(routeId)
        .autoStartup(false)
        .log("${headers[kafka.PARTITION]} ${body} ${threadName}")
        .process().body(String.class, b -> doSomeIOStuff(b))
        .process(e -> metricsService.inc());
  }

  private void doSomeIOStuff(String body) {
    try {
      File file = File.createTempFile("kafka", "temp");
      try (Writer writer = new FileWriter(file)) {
        for (int i = 0; i < 1000; i++) {
          IOUtils.write(body, writer);
        }
      }
      file.delete();
    } catch (Exception e) {
    }
  }
}

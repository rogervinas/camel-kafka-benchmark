package com.rogervinas.camelkafkabenchmark.kafka;

import com.rogervinas.camelkafkabenchmark.metrics.MetricsService;
import java.time.Duration;
import org.apache.camel.RoutesBuilder;
import org.apache.camel.test.junit4.CamelTestSupport;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaRouteBenchmark extends CamelTestSupport {

  private static final String ROUTE_ID = "kafka";
  private static final String HOST = "localhost:9092";
  private static final String GROUP_ID = "test";

  private static final int NUMBER_OF_MESSAGES = 10_000;
  private static final Duration EXECUTION_TIME = Duration.ofSeconds(20);

  private final int partitions;
  private final String topic;
  private final int consumersCount;
  private final int consumerStreams;
  private final MetricsService metricsService;

  public KafkaRouteBenchmark(int partitions, int consumersCount, int consumerStreams) {
    this.partitions = partitions;
    this.topic = "priv.test" + partitions;
    this.consumersCount = consumersCount;
    this.consumerStreams = consumerStreams;
    this.metricsService = new MetricsService();
  }

  @Override
  protected RoutesBuilder createRouteBuilder() {
    return new KafkaRouteBuilder(ROUTE_ID, HOST, topic, GROUP_ID, consumersCount, consumerStreams, metricsService);
  }

  public String execute() throws Exception {
    Producer<String, String> producer = KafkaProducerHelper.newProducer(HOST);
    for (int i=0; i< NUMBER_OF_MESSAGES; i++) {
      producer.send(new ProducerRecord<>(topic, i % partitions, "key" + i, "value-" + i));
    }
    metricsService.reset();
    context().startRoute(ROUTE_ID);
    Thread.sleep(EXECUTION_TIME.toMillis());
    context().stopRoute(ROUTE_ID);
    return metricsService.result().toString();
  }
}

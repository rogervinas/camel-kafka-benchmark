package com.rogervinas.camelkafkabenchmark.kafka;

import com.rogervinas.camelkafkabenchmark.metrics.Metrics;
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

  private final int partitions;
  private final String topic;
  private final int consumers;

  private int numberOfMessages = 10_000;
  private Duration executionDuration = Duration.ofSeconds(20);

  private final MetricsService metricsService;

  public KafkaRouteBenchmark(int partitions, int consumers) {
    this.partitions = partitions;
    this.topic = "priv.test" + partitions;
    this.consumers = consumers;
    this.metricsService = new MetricsService(partitions, consumers);
  }

  public KafkaRouteBenchmark withNumberOfMessages(int numberOfMessages) {
    this.numberOfMessages = numberOfMessages;
    return this;
  }

  public KafkaRouteBenchmark withExecutionDuration(Duration executionDuration) {
    this.executionDuration = executionDuration;
    return this;
  }

  @Override
  protected RoutesBuilder createRouteBuilder() {
    return new KafkaRouteBuilder(ROUTE_ID, HOST, topic, GROUP_ID, consumers, metricsService);
  }

  public Metrics execute() throws Exception {
    sendMessages();
    metricsService.reset();
    context().startRoute(ROUTE_ID);
    Thread.sleep(executionDuration.toMillis());
    context().stopRoute(ROUTE_ID);
    return metricsService.result();
  }

  private void sendMessages() {
    Producer<String, String> producer = KafkaProducerHelper.newProducer(HOST);
    for (int message=0; message<numberOfMessages; message++) {
      final int partition = message % partitions;
      producer.send(new ProducerRecord<>(topic, partition, "key" + message, "value-" + message));
    }
  }
}

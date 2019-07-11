package com.rogervinas.camelkafkabenchmark.kafka;

import java.util.Properties;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;

public class KafkaProducerHelper {

  private KafkaProducerHelper() {
  }

  public static Producer<String, String> newProducer(String bootstrapServers) {
    return new KafkaProducer<>(createProducerConfig(bootstrapServers));
  }

  private static Properties createProducerConfig(String bootstrapServers) {
    Properties config = new Properties();
    config.setProperty(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    config.setProperty(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    config.setProperty(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
    return config;
  }
}

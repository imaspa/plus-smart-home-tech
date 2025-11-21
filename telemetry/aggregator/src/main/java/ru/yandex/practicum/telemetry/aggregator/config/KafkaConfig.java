package ru.yandex.practicum.telemetry.aggregator.config;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@AllArgsConstructor
@ConfigurationProperties("aggregator.kafka")
public class KafkaConfig {
    private final KafkaConfigProducer producer;
    private final KafkaConfigConsumer consumer;
}
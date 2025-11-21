package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.aggregator.config.KafkaConfig;
import ru.yandex.practicum.telemetry.aggregator.config.KafkaConfigConsumer;
import ru.yandex.practicum.telemetry.aggregator.config.KafkaConfigProducer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@Slf4j
@Component
@RequiredArgsConstructor
public class SensorEventsAggregator {
    private final SnapshotService snapshotService;

    private final KafkaConfigConsumer consumerConfig;
    private final KafkaConfigProducer producerConfig;

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private final KafkaConsumer<String, SensorEventAvro> consumer;
    private final KafkaProducer<String, SensorsSnapshotAvro> producer;

    @Autowired
    public SensorEventsAggregator(SnapshotService snapshotService, KafkaConfig kafkaConfig) {
        this.snapshotService = snapshotService;
        this.consumerConfig = kafkaConfig.getConsumer();
        this.producerConfig = kafkaConfig.getProducer();

        this.consumer = new KafkaConsumer<>(consumerConfig.getProperties());
        this.producer = new KafkaProducer<>(producerConfig.getProperties());

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("[Aggregation][JVM STOP]. Остановка Consumer ");
            consumer.wakeup();
        }));
    }

    public void processSensorEvents() {
        try {
            log.info("[Aggregation] Subscribing to topic: {}", consumerConfig.getTopic());
            consumer.subscribe(List.of(consumerConfig.getTopic()));

            while (true) {
                ConsumerRecords<String, SensorEventAvro> records = consumer.poll(consumerConfig.getPollTimeout());
                if (!records.isEmpty()) {
                    int count = 0;
                    for (ConsumerRecord<String, SensorEventAvro> record : records) {
                        log.trace("[Aggregation] [PROCESSING] хаб: {}; партиция: {}; смещение: {}", record.key(), record.partition(), record.offset());
                        handleEvent(record.value());
                        trackAndCommitOffset(record, count++);
                    }
                    producer.flush();
                    consumer.commitAsync();
                }
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("[Aggregation] [PROCESSING ERROR]", e);
        } finally {
            shutdownResources();
        }
    }

    private void shutdownResources() {
        try {
            producer.flush();
            consumer.commitSync(currentOffsets);
        } finally {
            log.info("[Aggregation] Closing Kafka clients");
            consumer.close();
            producer.close();
        }
    }

    private void handleEvent(SensorEventAvro event) {
        Optional<SensorsSnapshotAvro> updatedState = snapshotService.updateState(event);

        if (updatedState.isPresent()) {
            SensorsSnapshotAvro snapshot = updatedState.get();
            sendSnapshotToKafka(snapshot);
        } else {
            log.trace("[Aggregation][EVENT NOT UPDATE SNAPSHOT] сенсор: {}; хаб: {}", event.getId(), event.getHubId());
        }
    }

    private void sendSnapshotToKafka(SensorsSnapshotAvro snapshot) {
        log.info("[Aggregation][EVENT CREATE SNAPSHOT] хаб: {}; топик: {};", snapshot.getHubId(), producerConfig.getTopic());

        ProducerRecord<String, SensorsSnapshotAvro> record = new ProducerRecord<>(producerConfig.getTopic(), null, snapshot.getTimestamp().toEpochMilli(), snapshot.getHubId(), snapshot);

        try {
            RecordMetadata metadata = producer.send(record).get();
            log.info("[Aggregation][EVENT CREATE SNAPSHOT OK] партиция: {}; смещение: {};", metadata.partition(), metadata.offset());
        } catch (InterruptedException | ExecutionException e) {
            log.warn("[Aggregation][EVENT CREATE SNAPSHOT ERR] топик {}", producerConfig.getTopic(), e);
        }
    }

    private void trackAndCommitOffset(ConsumerRecord<String, SensorEventAvro> record, int count) {
        currentOffsets.put(new TopicPartition(record.topic(), record.partition()), new OffsetAndMetadata(record.offset() + 1));

        if (count % 200 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("[Aggregation] [ERR] trackAndCommitOffset смещение: {}", offsets, exception);
                }
            });
        }
    }
}
package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.consumer.OffsetAndMetadata;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.telemetry.analyzer.configuration.KafkaConfig;
import ru.yandex.practicum.telemetry.analyzer.configuration.KafkaConfigConsumer;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class SnapshotProcessor {
    private final SnapshotAnalyser analyser;

    private final Map<TopicPartition, OffsetAndMetadata> currentOffsets = new HashMap<>();

    private final KafkaConsumer<String, SensorsSnapshotAvro> consumer;
    private final List<String> topics;
    private final Duration pollTimeout;

    @Autowired
    public SnapshotProcessor(SnapshotAnalyser analyser, KafkaConfig kafkaConfig) {
        this.analyser = analyser;

        final KafkaConfigConsumer consumerConfig = kafkaConfig.getConsumers().get(this.getClass().getSimpleName());

        this.consumer = new KafkaConsumer<>(consumerConfig.getProperties());
        this.topics = consumerConfig.getTopics();
        this.pollTimeout = consumerConfig.getPollTimeout();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("[Analyzer SnapShot][JVM STOP]. Остановка Consumer снапшотов ");
            consumer.wakeup();
        }));
    }

    public void processorSnapshot() {
        try {
            log.info("[Analyzer SnapShot] Subscribing to topic: {}", topics);
            consumer.subscribe(topics);

            while (true) {
                ConsumerRecords<String, SensorsSnapshotAvro> records = consumer.poll(pollTimeout);

                if (!records.isEmpty()) {
                    int count = 0;
                    for (ConsumerRecord<String, SensorsSnapshotAvro> record : records) {
                        analyser.process(record.value());
                        trackAndCommitOffset(record, count++);
                    }
                    consumer.commitAsync();
                }
            }
        } catch (WakeupException ignored) {
        } catch (Exception e) {
            log.error("[Analyzer SnapShot] [PROCESSING ERROR]", e);
        } finally {
            shutdownResources();
        }
    }

    private void shutdownResources() {
        try {
            consumer.commitSync(currentOffsets);
        } finally {
            log.info("[Analyzer SnapShot] Closing Kafka clients");
            consumer.close();
        }
    }

    private void trackAndCommitOffset(ConsumerRecord<?, ?> record, int count) {
        currentOffsets.put(
                new TopicPartition(record.topic(), record.partition()),
                new OffsetAndMetadata(record.offset() + 1)
        );

        if (count % 100 == 0) {
            consumer.commitAsync(currentOffsets, (offsets, exception) -> {
                if (exception != null) {
                    log.warn("[Analyzer SnapShot] [ERR] trackAndCommitOffset смещение: {}", offsets, exception);
                }
            });
        }
    }
}
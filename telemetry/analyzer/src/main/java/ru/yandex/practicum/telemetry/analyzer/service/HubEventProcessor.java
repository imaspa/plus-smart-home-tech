package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.telemetry.analyzer.configuration.KafkaConfig;
import ru.yandex.practicum.telemetry.analyzer.configuration.KafkaConfigConsumer;
import ru.yandex.practicum.telemetry.analyzer.model.Sensor;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Slf4j
@Component
public class HubEventProcessor implements Runnable {
    private final KafkaConsumer<String, HubEventAvro> consumer;
    private final SensorService sensorService;
    private final ScenarioService scenarioService;
    private final List<String> topics;
    private final Duration pollTimeout;

    public HubEventProcessor(KafkaConfig config, SensorService sensorService, ScenarioService scenarioService) {
        this.sensorService = sensorService;
        this.scenarioService = scenarioService;

        final KafkaConfigConsumer consumerConfig = config.getConsumers().get(this.getClass().getSimpleName());
        this.consumer = new KafkaConsumer<>(consumerConfig.getProperties());
        this.topics = consumerConfig.getTopics();
        this.pollTimeout = consumerConfig.getPollTimeout();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("[Analyzer Hub][JVM STOP]. Остановка Consumer хабов ");
            consumer.wakeup();
        }));
    }

    @Override
    public void run() {
        log.info("[Analyzer Hub] Subscribing to topic: {}", topics);
        consumer.subscribe(topics);
        try {
            while (true) {
                ConsumerRecords<String, HubEventAvro> records = consumer.poll(pollTimeout);
                if (!records.isEmpty()) {
                    for (ConsumerRecord<String, HubEventAvro> record : records) {
                        processEvent(record.value());
                    }
                    consumer.commitSync();
                }
            }
        } catch (WakeupException e) {
        } catch (Exception e) {
            log.error("[Analyzer Hub] [PROCESSING ERROR]", e);
        } finally {
            consumer.close();
        }
    }


    private void processEvent(HubEventAvro hubEvent) {
        String hubId = hubEvent.getHubId();
        switch (hubEvent.getPayload()) {
            case DeviceAddedEventAvro dae -> processEvent(hubId, dae);
            case DeviceRemovedEventAvro dre -> processEvent(hubId, dre);
            case ScenarioAddedEventAvro sae -> processEvent(hubId, sae);
            case ScenarioRemovedEventAvro sre -> processEvent(hubId, sre);
            default -> log.warn("[Analyzer Hub] [PROCESSEVENT ALARM] неизвестного событие: {}", hubEvent);
        }
    }

    private void processEvent(String hubId, DeviceAddedEventAvro event) {
        Optional<Sensor> maybeAdded = sensorService.findByIdAndHubId(hubId, event.getId());
        if (maybeAdded.isPresent()) {
            log.debug("[Analyzer Hub][SENSOR ALARM] уже зарегистрирован ид: {}; хаб: {}", event.getId(), hubId);
            return;
        }

        Sensor sensor = new Sensor();
        sensor.setHubId(hubId);
        sensor.setId(event.getId());

        log.debug("[Analyzer Hub][SENSOR ADD] ид: {}; хаб: {}", event.getId(), hubId);
        sensorService.save(sensor);
    }

    private void processEvent(String hubId, DeviceRemovedEventAvro event) {
        log.debug("[Analyzer Hub][SENSOR DEL] ид: {}; хаб: {}", event.getId(), hubId);
        sensorService
                .findByIdAndHubId(event.getId(), hubId)
                .ifPresent(sensorService::delete);
    }

    private void processEvent(String hubId, ScenarioAddedEventAvro event) {
        log.info("[Analyzer Hub][SCENARIO ADD] наименование: {}; хаб: {}", event.getName(), hubId);
        scenarioService.save(event, hubId);
    }

    private void processEvent(String hubId, ScenarioRemovedEventAvro event) {
        log.info("[Analyzer Hub][SCENARIO DEL] наименование: {}; хаб: {}", event.getName(), hubId);
        scenarioService.deleteScenario(event.getName(), hubId);
    }
}

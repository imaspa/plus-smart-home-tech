package ru.yandex.practicum.telemetry.aggregator.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorStateAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class SnapshotService {
    private final Map<String, SensorsSnapshotAvro> hubSnapshots = new HashMap<>();

    public Optional<SensorsSnapshotAvro> updateState(SensorEventAvro event) {
        String hubId = event.getHubId();
        String sensorId = event.getId();

        SensorsSnapshotAvro snapshot = hubSnapshots.computeIfAbsent(hubId,
                id -> createNewSnapshot(id, event.getTimestamp()));

        Map<String, SensorStateAvro> sensorsState = snapshot.getSensorsState();
        SensorStateAvro currentState = sensorsState.get(sensorId);

        if (shouldSkipEvent(currentState, event)) {
            return Optional.empty();
        }

        updateSensorState(snapshot, sensorsState, event);
        return Optional.of(snapshot);
    }

    private SensorsSnapshotAvro createNewSnapshot(String hubId, java.time.Instant timestamp) {
        return SensorsSnapshotAvro.newBuilder()
                .setHubId(hubId)
                .setTimestamp(timestamp)
                .setSensorsState(new HashMap<>())
                .build();
    }

    private boolean shouldSkipEvent(SensorStateAvro currentState, SensorEventAvro event) {
        if (currentState == null) {
            return false; // Новый датчик
        }
        boolean isStale = currentState.getTimestamp().isAfter(event.getTimestamp());
        boolean isDuplicate = currentState.getData().equals(event.getPayload());
        return isStale || isDuplicate;
    }

    private void updateSensorState(SensorsSnapshotAvro snapshot,
                                   Map<String, SensorStateAvro> sensorsState,
                                   SensorEventAvro event) {
        SensorStateAvro newState = SensorStateAvro.newBuilder()
                .setTimestamp(event.getTimestamp())
                .setData(event.getPayload())
                .build();

        sensorsState.put(event.getId(), newState);

        if (snapshot.getTimestamp().isBefore(event.getTimestamp())) {
            snapshot.setTimestamp(event.getTimestamp());
        }
        log.trace("[UPDATE] сенсор: {}; хаб: {}", event.getId(), event.getHubId());
    }
}
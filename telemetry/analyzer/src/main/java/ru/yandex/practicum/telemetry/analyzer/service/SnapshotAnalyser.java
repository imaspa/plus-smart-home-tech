package ru.yandex.practicum.telemetry.analyzer.service;

import com.google.protobuf.Timestamp;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionRequest;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.SensorsSnapshotAvro;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.telemetry.analyzer.model.Action;
import ru.yandex.practicum.telemetry.analyzer.model.Condition;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static ru.yandex.practicum.grpc.telemetry.hubrouter.HubRouterControllerGrpc.HubRouterControllerBlockingStub;
import static ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro.LUMINOSITY;
import static ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro.MOTION;
import static ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro.SWITCH;
import static ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro.TEMPERATURE;

@Slf4j
@Service
@Transactional(readOnly = true)
public class SnapshotAnalyser {
    private final ScenarioRepository scenarioRepository;
    private final HubRouterControllerBlockingStub hubRouterClient;


    public SnapshotAnalyser(ScenarioRepository scenarioRepository,
                            @GrpcClient("hub-router")
                            HubRouterControllerBlockingStub hubRouterClient) {
        this.scenarioRepository = scenarioRepository;
        this.hubRouterClient = hubRouterClient;
    }

    public void process(SensorsSnapshotAvro snapshot) {
        scenarioRepository
                .findByHubId(snapshot.getHubId())
                .stream()
                .filter(scenario -> checkConditions(scenario.getConditions(), snapshot))
                .forEach(this::executeActions);
    }

    private boolean checkConditions(Map<String, Condition> conditions, SensorsSnapshotAvro snapshot) {
        return conditions.entrySet()
                .stream()
                .allMatch(entry -> checkCondition(entry.getKey(), entry.getValue(), snapshot));
    }


    private boolean checkCondition(String sensorId, Condition condition, SensorsSnapshotAvro snapshot) {
        return Optional.ofNullable(snapshot.getSensorsState().get(sensorId))
                .map(sensorState -> checkSensorCondition(condition, sensorState.getData()))
                .orElse(false);
    }

    private boolean checkSensorCondition(Condition condition, Object sensorData) {
        return switch (sensorData) {
            case ClimateSensorAvro data -> checkClimateCondition(condition, data);
            case LightSensorAvro data -> checkLightCondition(condition, data);
            case MotionSensorAvro data -> checkMotionCondition(condition, data);
            case TemperatureSensorAvro data -> checkTemperatureCondition(condition, data);
            case SwitchSensorAvro data -> checkSwitchCondition(condition, data);
            default -> false;
        };
    }

    private boolean checkClimateCondition(Condition condition, ClimateSensorAvro data) {
        return switch (condition.getType()) {
            case TEMPERATURE -> condition.check(data.getTemperatureC());
            case CO2LEVEL -> condition.check(data.getCo2Level());
            case HUMIDITY -> condition.check(data.getHumidity());
            default -> false;
        };
    }

    private boolean checkLightCondition(Condition condition, LightSensorAvro data) {
        return condition.getType().equals(LUMINOSITY) && condition.check(data.getLuminosity());
    }

    private boolean checkMotionCondition(Condition condition, MotionSensorAvro data) {
        return condition.getType().equals(MOTION) && condition.check(data.getMotion() ? 1 : 0);
    }

    private boolean checkTemperatureCondition(Condition condition, TemperatureSensorAvro data) {
        return condition.getType().equals(TEMPERATURE) && condition.check(data.getTemperatureC());
    }

    private boolean checkSwitchCondition(Condition condition, SwitchSensorAvro data) {
        return condition.getType().equals(SWITCH) && condition.check(data.getState() ? 1 : 0);
    }

    private void executeActions(Scenario scenario) {
        log.debug("[SnapshotAnalyser] Сработал сценарий: {}; хаб: {};", scenario.getName(), scenario.getHubId());

        var timestamp = createTimestamp();

        scenario.getActions().entrySet()
                .forEach(entry -> executeAction(entry.getValue(), entry.getKey(), scenario, timestamp));
    }

    private void executeAction(Action action, String sensorId, Scenario scenario, Timestamp timestamp) {
        try {
            var deviceAction = buildDeviceAction(action, sensorId);
            var request = DeviceActionRequest.newBuilder()
                    .setHubId(scenario.getHubId())
                    .setScenarioName(scenario.getName())
                    .setAction(deviceAction)
                    .setTimestamp(timestamp)
                    .build();

            hubRouterClient.handleDeviceAction(request);
        } catch (Exception e) {
            log.error("[SnapshotAnalyser][executeAction ERR] действие: {}; устройство: {}; хаб: {}"
                    , action.getType(), sensorId, scenario.getHubId(), e);
        }
    }

    private DeviceActionProto buildDeviceAction(Action action, String sensorId) {
        var builder = DeviceActionProto.newBuilder()
                .setSensorId(sensorId)
                .setType(mapActionType(action.getType()));

        if (action.getType().equals(ActionTypeAvro.SET_VALUE)) {
            builder.setValue(action.getValue());
        }

        return builder.build();
    }

    private ActionTypeProto mapActionType(ActionTypeAvro avro) {
        return ActionTypeProto.valueOf(avro.name());
    }

    private Timestamp createTimestamp() {
        Instant ts = Instant.now();
        return Timestamp.newBuilder()
                .setSeconds(ts.getEpochSecond())
                .setNanos(ts.getNano())
                .build();
    }
}

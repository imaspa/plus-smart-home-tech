package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.collector.model.constant.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.hub.DeviceAction;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.ScenarioCondition;
import ru.yandex.practicum.telemetry.collector.model.hub.event.ScenarioAddedEvent;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.yandex.practicum.telemetry.collector.utils.EnumMapper;

@Component
public class ScenarioAddedHubEventHandler extends BaseHubEventHandler<ScenarioAddedEventAvro> {

    public ScenarioAddedHubEventHandler(KafkaEventProducer producer) {
        super(producer);
    }

    @Override
    public HubEventType getMessageType() {
        return HubEventType.SCENARIO_ADDED;
    }

    @Override
    protected ScenarioAddedEventAvro mapToAvro(HubEvent event) {
        ScenarioAddedEvent _event = (ScenarioAddedEvent) event;
        return ScenarioAddedEventAvro.newBuilder()
                .setName(_event.getName())
                .setActions(
                        _event.getActions()
                                .stream()
                                .map(this::mapToAvro)
                                .toList()
                )
                .setConditions(
                        _event.getConditions()
                                .stream()
                                .map(this::mapToAvro)
                                .toList()
                )
                .build();
    }

    private DeviceActionAvro mapToAvro(DeviceAction action) {
        return DeviceActionAvro.newBuilder()
                .setSensorId(action.getSensorId())
                .setType(EnumMapper.map(action.getType(), ActionTypeAvro.class))
                .setValue(action.getValue())
                .build();
    }

    private ScenarioConditionAvro mapToAvro(ScenarioCondition condition) {
        return ScenarioConditionAvro.newBuilder()
                .setOperation(EnumMapper.map(condition.getOperation(), ConditionOperationAvro.class))
                .setSensorId(condition.getSensorId())
                .setType(EnumMapper.map(condition.getType(), ConditionTypeAvro.class))
                .setValue(condition.getValue())
                .build();
    }
}
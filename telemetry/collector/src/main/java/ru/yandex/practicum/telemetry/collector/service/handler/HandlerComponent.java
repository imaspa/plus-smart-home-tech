package ru.yandex.practicum.telemetry.collector.service.handler;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.collector.model.constant.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.constant.SensorEventType;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Component
public class HandlerComponent {
    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventType, HubEventHandler> hubEventHandlers;

    public HandlerComponent(Set<SensorEventHandler> sensorEventHandlers,
                            Set<HubEventHandler> hubEventHandlers) {
        this.sensorEventHandlers = sensorEventHandlers.stream()
                .collect(Collectors.toMap(
                        SensorEventHandler::getMessageType,
                        Function.identity()
                ));

        this.hubEventHandlers = hubEventHandlers.stream()
                .collect(Collectors.toMap(
                        HubEventHandler::getMessageType,
                        Function.identity()
                ));
    }
}

package ru.yandex.practicum.telemetry.collector.service.handler;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.collector.model.constant.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.constant.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Component
public class HandlerComponent {

    private final Map<SensorEventType, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventType, HubEventHandler> hubEventHandlers;

    private final SensorEventHandler defaultSensorHandler = new SensorEventHandler() {
        @Override
        public SensorEventType getMessageType() {
            throw new UnsupportedOperationException("Default handler (has no type)");
        }

        @Override
        public void handle(SensorEvent event) {
            throw new IllegalArgumentException(
                    "Не найден обработчик для события сенсора: %s".formatted(event.getType())
            );
        }
    };

    private final HubEventHandler defaultHubHandler = new HubEventHandler() {
        @Override
        public HubEventType getMessageType() {
            throw new UnsupportedOperationException("Default handler (has no type)");
        }

        @Override
        public void handle(HubEvent event) {
            throw new IllegalArgumentException(
                    "Не найден обработчик для событий хаба: %s".formatted(event.getType())
            );
        }
    };

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

    public SensorEventHandler getSensorHandler(SensorEvent event) {
        return sensorEventHandlers.getOrDefault(event.getType(), defaultSensorHandler);
    }

    public HubEventHandler getHubHandler(HubEvent event) {
        return hubEventHandlers.getOrDefault(event.getType(), defaultHubHandler);
    }
}

package ru.yandex.practicum.telemetry.collector.service.handler;

import lombok.Getter;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.grpc.telemetry.event.SensorEventProto;

import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
@Component
public class HandlerComponent {

    private final Map<SensorEventProto.PayloadCase, SensorEventHandler> sensorEventHandlers;
    private final Map<HubEventProto.PayloadCase, HubEventHandler> hubEventHandlers;

    private final SensorEventHandler defaultSensorHandler = new SensorEventHandler() {
        @Override
        public SensorEventProto.PayloadCase getMessageType() {
            throw new UnsupportedOperationException("Default handler (has no type)");
        }

        @Override
        public void handle(SensorEventProto event) {
            throw new IllegalArgumentException(
                    "Не найден обработчик для события сенсора: %s".formatted(event.getPayloadCase())
            );
        }
    };

    private final HubEventHandler defaultHubHandler = new HubEventHandler() {
        @Override
        public HubEventProto.PayloadCase getMessageType() {
            throw new UnsupportedOperationException("Default handler (has no type)");
        }

        @Override
        public void handle(HubEventProto event) {
            throw new IllegalArgumentException(
                    "Не найден обработчик для событий хаба: %s".formatted(event.getPayloadCase())
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

    public SensorEventHandler getSensorHandler(SensorEventProto event) {
        return sensorEventHandlers.getOrDefault(event.getPayloadCase(), defaultSensorHandler);
    }

    public HubEventHandler getHubHandler(HubEventProto event) {
        return hubEventHandlers.getOrDefault(event.getPayloadCase(), defaultHubHandler);
    }
}

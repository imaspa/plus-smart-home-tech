package ru.yandex.practicum.telemetry.collector.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;
import ru.yandex.practicum.telemetry.collector.service.handler.HandlerComponent;
import ru.yandex.practicum.telemetry.collector.service.handler.HubEventHandler;
import ru.yandex.practicum.telemetry.collector.service.handler.SensorEventHandler;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EventRestController {

    private final HandlerComponent handlerComponent;

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent request) {
        log.debug("[REQUEST] sensors: {}", request);
        SensorEventHandler handler = handlerComponent.getSensorEventHandlers().get(request.getType());
        if (handler == null) {
            throw new IllegalArgumentException("Не найден обработчик события: %s".formatted(request.getType()));
        }
        handler.handle(request);
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent request) {
        log.debug("[REQUEST] hubs: {}", request);
        HubEventHandler handler = handlerComponent.getHubEventHandlers().get(request.getType());
        if (handler == null) {
            throw new IllegalArgumentException("Не найден обработчик события: %s".formatted(request.getType()));
        }
        handler.handle(request);
    }

}


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

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/events", consumes = MediaType.APPLICATION_JSON_VALUE)
@RequiredArgsConstructor
public class EventRestController {

    private final HandlerComponent handlerComponent;

    @PostMapping("/sensors")
    public void collectSensorEvent(@Valid @RequestBody SensorEvent request) {
        log.info("[REQUEST] sensors: id sensor: {}; id хаба: {}", request.getId(), request.getHubId());
        log.debug("[REQUEST] sensors (тело): {}", request);
        handlerComponent.getSensorHandler(request).handle(request);
    }

    @PostMapping("/hubs")
    public void collectHubEvent(@Valid @RequestBody HubEvent request) {
        log.info("[REQUEST] hubs: id хаба: {}", request.getHubId());
        log.debug("[REQUEST] hubs: {}", request);
        handlerComponent.getHubHandler(request).handle(request);
    }

}


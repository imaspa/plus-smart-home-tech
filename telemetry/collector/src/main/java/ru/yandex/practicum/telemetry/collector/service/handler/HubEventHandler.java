package ru.yandex.practicum.telemetry.collector.service.handler;

import ru.yandex.practicum.telemetry.collector.model.constant.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;

public interface HubEventHandler {

    HubEventType getMessageType();

    void handle(HubEvent event);
}
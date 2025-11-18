package ru.yandex.practicum.telemetry.collector.service.handler;

import ru.yandex.practicum.telemetry.collector.model.constant.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;

public interface SensorEventHandler {

    SensorEventType getMessageType();

    void handle(SensorEvent event);
}
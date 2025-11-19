package ru.yandex.practicum.telemetry.collector.model.constant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Перечисление типов событий датчиков. Определяет различные типы событий, которые могут быть связаны с датчиками.")
public enum SensorEventType {

    @Schema(description = "Событие от датчика движения.")
    MOTION_SENSOR_EVENT,

    @Schema(description = "Событие от датчика температуры.")
    TEMPERATURE_SENSOR_EVENT,

    @Schema(description = "Событие от датчика освещенности.")
    LIGHT_SENSOR_EVENT,

    @Schema(description = "Событие от климатического датчика.")
    CLIMATE_SENSOR_EVENT,

    @Schema(description = "Событие от переключателя.")
    SWITCH_SENSOR_EVENT
}
package ru.yandex.practicum.telemetry.collector.model.constant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Перечисление типов устройств, которые могут быть добавлены в систему.")
public enum DeviceType {

    @Schema(description = "Датчик движения.")
    MOTION_SENSOR,

    @Schema(description = "Датчик температуры.")
    TEMPERATURE_SENSOR,

    @Schema(description = "Датчик освещенности.")
    LIGHT_SENSOR,

    @Schema(description = "Климатический датчик.")
    CLIMATE_SENSOR,

    @Schema(description = "Переключатель.")
    SWITCH_SENSOR
}
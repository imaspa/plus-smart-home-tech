package ru.yandex.practicum.telemetry.collector.model.constant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Типы условий, которые могут использоваться в сценариях.")
public enum ConditionType {

    @Schema(description = "Условие на наличие/отсутствие движения.")
    MOTION,

    @Schema(description = "Условие на процент освещенности")
    LUMINOSITY,

    @Schema(description = "Условие на наличие состояния включено/выключено")
    SWITCH,

    @Schema(description = "Условие на показания температуры.")
    TEMPERATURE,

    @Schema(description = "Условие на уровень CO2.")
    CO2LEVEL,

    @Schema(description = "Условия на уровень влажности.")
    HUMIDITY
}
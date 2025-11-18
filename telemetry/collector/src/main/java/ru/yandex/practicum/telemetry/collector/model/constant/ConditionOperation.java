package ru.yandex.practicum.telemetry.collector.model.constant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Операции, которые могут быть использованы в условиях.")
public enum ConditionOperation {

    @Schema(description = "Равно.")
    EQUALS,

    @Schema(description = "Больше чем.")
    GREATER_THAN,

    @Schema(description = "Меньше чем.")
    LOWER_THAN
}
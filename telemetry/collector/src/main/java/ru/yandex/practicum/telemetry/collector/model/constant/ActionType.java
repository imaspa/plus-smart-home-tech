package ru.yandex.practicum.telemetry.collector.model.constant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Перечисление возможных типов действий при срабатывании условия активации сценария.")
public enum ActionType {

    @Schema(description = "Активировать.")
    ACTIVATE,

    @Schema(description = "Деактивировать.")
    DEACTIVATE,

    @Schema(description = "Изменить значение на противоположное.")
    INVERSE,

    @Schema(description = "Установить значение.")
    SET_VALUE
}
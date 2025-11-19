package ru.yandex.practicum.telemetry.collector.model.constant;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Перечисление типов событий хаба.")
public enum HubEventType {

    @Schema(description = "Событие добавления устройства.")
    DEVICE_ADDED,

    @Schema(description = "Событие удаления устройства.")
    DEVICE_REMOVED,

    @Schema(description = "Событие добавления сценария.")
    SCENARIO_ADDED,

    @Schema(description = "Событие удаления сценария.")
    SCENARIO_REMOVED
}

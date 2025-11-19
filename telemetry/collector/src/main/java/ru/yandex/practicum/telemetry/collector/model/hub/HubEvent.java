package ru.yandex.practicum.telemetry.collector.model.hub;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.constant.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.hub.event.DeviceAddedEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.event.DeviceRemovedEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.event.ScenarioAddedEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.event.ScenarioRemovedEvent;

import java.time.Instant;


@Getter
@Setter
@ToString
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type"
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = DeviceAddedEvent.class, name = "DEVICE_ADDED"),
        @JsonSubTypes.Type(value = DeviceRemovedEvent.class, name = "DEVICE_REMOVED"),
        @JsonSubTypes.Type(value = ScenarioAddedEvent.class, name = "SCENARIO_ADDED"),
        @JsonSubTypes.Type(value = ScenarioRemovedEvent.class, name = "SCENARIO_REMOVED")
})
@Schema(description = "Абстрактный класс для представления событий хаба.")
public abstract class HubEvent {

    @Schema(description = "Идентификатор хаба, связанный с событием.")
    @NotNull
    private String hubId;

    @Schema(description = "Временная метка события. По умолчанию устанавливается текущее время.")
    private Instant timestamp = Instant.now();

    @NotNull
    @Schema(description = "Тип события, который определяет конкретный тип события, например, добавление устройства или удаление сценария.")
    public abstract HubEventType getType();
}
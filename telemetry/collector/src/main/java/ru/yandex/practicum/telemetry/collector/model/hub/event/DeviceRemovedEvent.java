package ru.yandex.practicum.telemetry.collector.model.hub.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.constant.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;

@Getter
@Setter
@ToString(callSuper = true)
@Schema(description = "Событие, сигнализирующее о удалении устройства из системы.")
@NotNull
public class DeviceRemovedEvent extends HubEvent {

    @Schema(description = "Идентификатор удаленного устройства.")
    @NotBlank
    private String id;

    @Override
    @Schema(description = "Тип события удаления устройства.")
    public HubEventType getType() {
        return HubEventType.DEVICE_REMOVED;
    }
}
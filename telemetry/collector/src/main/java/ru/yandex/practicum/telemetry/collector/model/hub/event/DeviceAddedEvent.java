package ru.yandex.practicum.telemetry.collector.model.hub.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.constant.DeviceType;
import ru.yandex.practicum.telemetry.collector.model.constant.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;

@Getter
@Setter
@ToString(callSuper = true)
@Schema(description = "Событие, сигнализирующее о добавлении нового устройства в систему.")
@NotNull
public class DeviceAddedEvent extends HubEvent {

    @Schema(description = "Идентификатор добавленного устройства.")
    @NotBlank
    private String id;

    @Schema(description = "Тип добавленного устройства.")
    @NotNull
    private DeviceType deviceType;

    @Override
    @Schema(description = "Тип события добавления устройства.")
    public HubEventType getType() {
        return HubEventType.DEVICE_ADDED;
    }
}

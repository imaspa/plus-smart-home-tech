package ru.yandex.practicum.telemetry.collector.model.hub;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.annotation.Nullable;
import lombok.Data;
import ru.yandex.practicum.telemetry.collector.model.constant.ActionType;


@Data
@Schema(description = "Представляет действие, которое должно быть выполнено устройством.")
public class DeviceAction {

    @Schema(description = "Идентификатор датчика, связанного с действием.")
    private String sensorId;

    @Schema(description = "Тип действия, которое должно быть выполнено.")
    private ActionType type;

    @Schema(description = "Необязательное значение, связанное с действием.")
    private @Nullable Integer value;
}
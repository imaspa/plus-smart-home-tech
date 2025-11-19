package ru.yandex.practicum.telemetry.collector.model.sensor.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.constant.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.sensor.SensorEvent;

@Getter
@Setter
@ToString(callSuper = true)
@Schema(description = "Событие датчика переключателя, содержащее информацию о текущем состоянии переключателя.")
public class SwitchSensorEvent extends SensorEvent {

    @Schema(description = "Текущее состояние переключателя. true - включен, false - выключен.")
    @NotNull
    private boolean state;

    @Override
    @Schema(description = "Тип события датчика переключателя.")
    public SensorEventType getType() {
        return SensorEventType.SWITCH_SENSOR_EVENT;
    }
}

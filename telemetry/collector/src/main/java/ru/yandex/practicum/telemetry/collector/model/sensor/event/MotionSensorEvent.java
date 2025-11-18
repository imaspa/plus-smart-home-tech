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
@Schema(description = "Событие датчика движения.")
public class MotionSensorEvent extends SensorEvent {

    @Schema(description = "Качество связи.")
    @NotNull
    private int linkQuality;

    @Schema(description = "Наличие/отсутствие движения.")
    @NotNull
    private boolean motion;

    @Schema(description = "Напряжение.")
    @NotNull
    private int voltage;

    @Override
    @Schema(description = "Тип события датчика движения.")
    public SensorEventType getType() {
        return SensorEventType.MOTION_SENSOR_EVENT;
    }
}
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
@Schema(description = "Событие датчика освещенности, содержащее информацию о качестве связи и уровне освещенности.")
@NotNull
public class LightSensorEvent extends SensorEvent {

    @Schema(description = "Качество связи.")
    private int linkQuality;

    @Schema(description = "Уровень освещенности.")
    private int luminosity;

    @Override
    @Schema(description = "Тип события датчика освещенности.")
    public SensorEventType getType() {
        return SensorEventType.LIGHT_SENSOR_EVENT;
    }
}
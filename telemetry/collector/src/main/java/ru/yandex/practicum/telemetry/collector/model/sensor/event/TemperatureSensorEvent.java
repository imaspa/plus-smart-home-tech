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
@Schema(description = "Событие датчика температуры, содержащее информацию о температуре в градусах Цельсия и Фаренгейта.")
public class TemperatureSensorEvent extends SensorEvent {

    @Schema(description = "Температура в градусах Цельсия.")
    @NotNull
    private int temperatureC;

    @Schema(description = "Температура в градусах Фаренгейта.")
    @NotNull
    private int temperatureF;

    @Override
    @Schema(description = "Тип события датчика температуры.")
    public SensorEventType getType() {
        return SensorEventType.TEMPERATURE_SENSOR_EVENT;
    }
}

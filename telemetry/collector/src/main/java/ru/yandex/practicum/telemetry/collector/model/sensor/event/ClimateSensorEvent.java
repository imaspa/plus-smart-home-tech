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
@Schema(description = "Событие климатического датчика, содержащее информацию о температуре, влажности и уровне CO2.")
@NotNull
public class ClimateSensorEvent extends SensorEvent {

    @Schema(description = "Уровень температуры по шкале Цельсия.")
    @NotNull
    private int temperatureC;

    @Schema(description = "Влажность.")
    @NotNull
    private int humidity;

    @Schema(description = "Уровень CO2.")
    @NotNull
    private int co2Level;

    @Override
    @Schema(description = "Тип события климатического датчика.")
    public SensorEventType getType() {
        return SensorEventType.CLIMATE_SENSOR_EVENT;
    }
}

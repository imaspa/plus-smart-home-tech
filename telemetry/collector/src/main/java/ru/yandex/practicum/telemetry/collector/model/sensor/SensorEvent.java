package ru.yandex.practicum.telemetry.collector.model.sensor;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.constant.SensorEventType;
import ru.yandex.practicum.telemetry.collector.model.sensor.event.ClimateSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.event.LightSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.event.MotionSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.event.SwitchSensorEvent;
import ru.yandex.practicum.telemetry.collector.model.sensor.event.TemperatureSensorEvent;

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
        @JsonSubTypes.Type(value = ClimateSensorEvent.class, name = "CLIMATE_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = LightSensorEvent.class, name = "LIGHT_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = MotionSensorEvent.class, name = "MOTION_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = SwitchSensorEvent.class, name = "SWITCH_SENSOR_EVENT"),
        @JsonSubTypes.Type(value = TemperatureSensorEvent.class, name = "TEMPERATURE_SENSOR_EVENT")
})
@Schema(description = "Абстрактный класс для представления событий датчиков. Используется для различных типов событий датчиков, таких как климатический датчик, датчик освещенности и т.д.",
        subTypes = {
                ClimateSensorEvent.class,
                LightSensorEvent.class,
                MotionSensorEvent.class,
                SwitchSensorEvent.class,
                TemperatureSensorEvent.class
        })
public abstract class SensorEvent {

    @Schema(description = "Идентификатор события датчика.")
    @NotBlank
    private String id;

    @Schema(description = "Идентификатор хаба, связанного с событием.")
    @NotNull
    private String hubId;

    @Schema(description = "Временная метка события. По умолчанию устанавливается текущее время.")
    private Instant timestamp = Instant.now();

    @NotNull
    @Schema(description = "Тип события, который определяет конкретный тип события датчика.")
    public abstract SensorEventType getType();
}
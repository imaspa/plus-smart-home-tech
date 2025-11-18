package ru.yandex.practicum.telemetry.collector.model.hub;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import ru.yandex.practicum.telemetry.collector.model.constant.ConditionOperation;
import ru.yandex.practicum.telemetry.collector.model.constant.ConditionType;

@Data
@Schema(description = "Условие сценария, которое содержит информацию о датчике, типе условия, операции и значении.")
public class ScenarioCondition {

    @Schema(description = "Идентификатор датчика, связанного с условием.")
    private String sensorId;

    @Schema(description = "Тип условия.")
    private ConditionType type;

    @Schema(description = "Операция, которая применяется в условии.")
    private ConditionOperation operation;

    @Schema(description = "Значение, используемое в условии.")
    private Integer value;
}
package ru.yandex.practicum.telemetry.collector.model.hub.event;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import ru.yandex.practicum.telemetry.collector.model.constant.HubEventType;
import ru.yandex.practicum.telemetry.collector.model.hub.DeviceAction;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.model.hub.ScenarioCondition;

import java.util.List;

@Getter
@Setter
@ToString(callSuper = true)
@Schema(description = "Событие добавления сценария в систему. Содержит информацию о названии сценария, условиях и действиях.")
public class ScenarioAddedEvent extends HubEvent {

    @Schema(description = "Название добавленного сценария. Должно содержать не менее 3 символов.")
    @NotBlank
    @Size(min = 3)
    private String name;

    @Schema(description = "Список условий, которые связаны со сценарием. Не может быть пустым.")
    @NotEmpty
    private List<ScenarioCondition> conditions;

    @Schema(description = "Список действий, которые должны быть выполнены в рамках сценария. Не может быть пустым.")
    @NotEmpty
    private List<DeviceAction> actions;

    @Override
    @Schema(description = "Тип события добавления сценария.")
    public HubEventType getType() {
        return HubEventType.SCENARIO_ADDED;
    }
}
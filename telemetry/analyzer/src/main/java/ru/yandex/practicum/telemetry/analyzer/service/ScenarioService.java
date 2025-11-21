package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.kafka.telemetry.event.ActionTypeAvro;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.telemetry.analyzer.exceptions.IllegalStateException;
import ru.yandex.practicum.telemetry.analyzer.model.Action;
import ru.yandex.practicum.telemetry.analyzer.model.Condition;
import ru.yandex.practicum.telemetry.analyzer.model.ConditionOperation;
import ru.yandex.practicum.telemetry.analyzer.model.Scenario;
import ru.yandex.practicum.telemetry.analyzer.repository.ActionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ConditionRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.ScenarioRepository;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ScenarioService {

    private final ScenarioRepository scenarioRepository;
    private final ConditionRepository conditionRepository;
    private final ActionRepository actionRepository;
    private final SensorRepository sensorRepository;

    @Transactional
    public Scenario save(ScenarioAddedEventAvro event, String hubId) {
        validateSensorsExist(event, hubId);

        Scenario scenario = findOrCreateScenario(hubId, event.getName());
        clearExistingScenarioData(scenario);

        addConditionsToScenario(scenario, event.getConditions());
        addActionsToScenario(scenario, event.getActions());

        return persistScenario(scenario);
    }

    private void addConditionsToScenario(Scenario scenario, Iterable<ScenarioConditionAvro> eventConditions) {
        for (ScenarioConditionAvro eventCondition : eventConditions) {
            Condition condition = new Condition();
            condition.setType(eventCondition.getType());
            condition.setOperation(ConditionOperation.from(eventCondition.getOperation()));
            condition.setValue(mapValue(eventCondition.getValue()));
            scenario.addCondition(eventCondition.getSensorId(), condition);
        }
    }

    private void addActionsToScenario(Scenario scenario, Iterable<DeviceActionAvro> eventActions) {
        for (DeviceActionAvro eventAction : eventActions) {
            Action action = new Action();
            action.setType(eventAction.getType());
            if (eventAction.getType().equals(ActionTypeAvro.SET_VALUE)) {
                action.setValue(mapValue(eventAction.getValue()));
            }
            scenario.addAction(eventAction.getSensorId(), action);
        }
    }

    private void validateSensorsExist(ScenarioAddedEventAvro event, String hubId) {
        Set<String> sensors = new HashSet<>();
        event.getConditions().forEach(condition -> sensors.add(condition.getSensorId()));
        event.getActions().forEach(action -> sensors.add(action.getSensorId()));

        if (!sensorRepository.existsByIdInAndHubId(sensors, hubId)) {
            throw new IllegalStateException("Сценарии можно создавать только с известными устройствами");
        }
    }

    private Scenario persistScenario(Scenario scenario) {
        conditionRepository.saveAll(scenario.getConditions().values());
        actionRepository.saveAll(scenario.getActions().values());
        return scenarioRepository.save(scenario);
    }

    private Scenario findOrCreateScenario(String hubId, String scenarioName) {
        return scenarioRepository.findByHubIdAndName(hubId, scenarioName)
                .orElseGet(() -> {
                    Scenario scenario = new Scenario();
                    scenario.setName(scenarioName);
                    scenario.setHubId(hubId);
                    return scenario;
                });
    }

    private void clearExistingScenarioData(Scenario scenario) {
        Map<String, Condition> conditions = scenario.getConditions();
        Map<String, Action> actions = scenario.getActions();

        if (!conditions.isEmpty()) {
            conditionRepository.deleteAll(conditions.values());
            scenario.getConditions().clear();
        }

        if (!actions.isEmpty()) {
            actionRepository.deleteAll(actions.values());
            scenario.getActions().clear();
        }
    }

    @Transactional
    public void deleteScenario(String name, String hubId) {
        Optional<Scenario> optScenario = scenarioRepository.findByHubIdAndName(hubId, name);
        if (optScenario.isPresent()) {
            Scenario scenario = optScenario.get();
            conditionRepository.deleteAll(scenario.getConditions().values());
            actionRepository.deleteAll(scenario.getActions().values());
            scenarioRepository.delete(scenario);
        }
    }

    private Integer mapValue(Object value) {
        if (value != null) {
            if (value instanceof Integer i) return i;
            if (value instanceof Boolean b) return b ? 1 : 0;
        }
        return null;
    }
}

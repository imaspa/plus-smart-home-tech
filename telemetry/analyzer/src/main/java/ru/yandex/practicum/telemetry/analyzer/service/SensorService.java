package ru.yandex.practicum.telemetry.analyzer.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.telemetry.analyzer.model.Sensor;
import ru.yandex.practicum.telemetry.analyzer.repository.SensorRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class SensorService {
    private final SensorRepository repository;

    @Transactional
    public Sensor save(Sensor sensor) {
        return repository.save(sensor);
    }

    @Transactional(readOnly = true)
    public Optional<Sensor> findByIdAndHubId(String id, String hubId) {
        return repository.findByIdAndHubId(id, hubId);
    }

    @Transactional
    public void delete(Sensor sensor) {
        repository.delete(sensor);
    }
}
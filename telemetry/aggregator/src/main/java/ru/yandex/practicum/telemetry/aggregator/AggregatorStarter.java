package ru.yandex.practicum.telemetry.aggregator;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.telemetry.aggregator.service.SensorEventsAggregator;

@Component
@RequiredArgsConstructor
public class AggregatorStarter {
    private final SensorEventsAggregator aggregator;

    @EventListener(ApplicationReadyEvent.class)
    public void onApplicationReady() {
        aggregator.processSensorEvents();
    }
}

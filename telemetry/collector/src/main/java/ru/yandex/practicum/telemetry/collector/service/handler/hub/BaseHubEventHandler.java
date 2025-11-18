package ru.yandex.practicum.telemetry.collector.service.handler.hub;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.telemetry.collector.model.hub.HubEvent;
import ru.yandex.practicum.telemetry.collector.service.KafkaEventProducer;
import ru.yandex.practicum.telemetry.collector.service.handler.HubEventHandler;

import static ru.yandex.practicum.telemetry.collector.configuration.TopicType.HUBS_EVENTS;

@Slf4j
@RequiredArgsConstructor
public abstract class BaseHubEventHandler<T extends SpecificRecordBase> implements HubEventHandler {

    protected final KafkaEventProducer producer;

    protected abstract T mapToAvro(HubEvent event);

    @Override
    public void handle(HubEvent event) {
        if (!event.getType().equals(getMessageType())) {
            throw new IllegalArgumentException("Неизвестный тип события: %s".formatted(event.getType()));
        }

        T payload = mapToAvro(event);

        HubEventAvro eventAvro = HubEventAvro.newBuilder()
                .setHubId(event.getHubId())
                .setTimestamp(event.getTimestamp())
                .setPayload(payload)
                .build();
//        log.debug("[JSON BEFORE TO AVRO] hubs: {}", event);
        producer.send(eventAvro, event.getHubId(), event.getTimestamp(), HUBS_EVENTS);
    }
}
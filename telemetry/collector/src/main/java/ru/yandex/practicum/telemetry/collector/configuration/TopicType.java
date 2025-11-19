package ru.yandex.practicum.telemetry.collector.configuration;

public enum TopicType {
    SENSORS_EVENTS,
    HUBS_EVENTS;

    public static TopicType from(String type) {
        if (type == null || type.isBlank()) {
            throw new IllegalArgumentException("Топик должен быть определен");
        }
        String normalized = type.replace('-', '_').toUpperCase().trim();
        try {
            return valueOf(normalized);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Неизвестный тип топика: %s".formatted(type));
        }
    }

}

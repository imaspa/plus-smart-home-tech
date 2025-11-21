package ru.yandex.practicum.telemetry.analyzer.model;

public interface Operation {
    boolean apply(int left, Integer right);
}

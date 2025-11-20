package ru.yandex.practicum.telemetry.analyzer.dal.entity;

public interface Operation {
    boolean apply(Integer left, Integer right);
}

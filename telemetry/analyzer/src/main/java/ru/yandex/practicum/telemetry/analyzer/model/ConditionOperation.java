package ru.yandex.practicum.telemetry.analyzer.model;

import ru.yandex.practicum.kafka.telemetry.event.ConditionOperationAvro;
import ru.yandex.practicum.telemetry.analyzer.exceptions.IllegalArgumentException;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.stream.Collectors;

public enum ConditionOperation implements Operation {
    EQUALS((l, r) -> r != null && l == r),
    GREATER_THAN((l, r) -> r != null && l > r),
    LOWER_THAN((l, r) -> r != null && l < r);

    private final BiPredicate<Integer, Integer> predicate;

    ConditionOperation(BiPredicate<Integer, Integer> predicate) {
        this.predicate = predicate;
    }

    @Override
    public boolean apply(int left, Integer right) {
        return predicate.test(left, right);
    }

    private static final Map<String, ConditionOperation> BY_NAME = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(op -> op.name().toLowerCase(), Function.identity()));

    public static ConditionOperation from(ConditionOperationAvro avro) {
        String key = Objects.requireNonNull(avro, "operation").name().toLowerCase();
        ConditionOperation op = BY_NAME.get(key);
        if (op == null) {
            throw new IllegalArgumentException("Неизвестный тип операции: " + avro.name());
        }
        return op;
    }
}

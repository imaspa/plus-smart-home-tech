package ru.yandex.practicum.telemetry.analyzer.dal.entity;

import lombok.Getter;
import lombok.Setter;
import ru.yandex.practicum.kafka.telemetry.event.ConditionTypeAvro;
import sun.jvm.hotspot.debugger.cdbg.EnumType;

import java.beans.Transient;

@Entity
@Getter
@Setter
@Table(name = "conditions")
public class Condition {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private ConditionTypeAvro type;

    @Enumerated(EnumType.STRING)
    private ConditionOperation operation;

    private Integer value;

    @Transient
    public boolean check(int sensorValue) {
        return operation.apply(sensorValue, this.value);
    }
}

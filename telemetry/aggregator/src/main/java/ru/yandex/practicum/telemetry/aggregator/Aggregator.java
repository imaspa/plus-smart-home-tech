package ru.yandex.practicum.telemetry.aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import ru.yandex.practicum.telemetry.aggregator.config.KafkaConfig;

@SpringBootApplication
@EnableConfigurationProperties(KafkaConfig.class)
public class Aggregator {

    public static void main(String[] args) {
        SpringApplication.run(Aggregator.class, args);
    }
}
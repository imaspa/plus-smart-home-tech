package ru.yandex.practicum.delivery.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.interaction.api.feign.client.OrderFeignClient;
import ru.yandex.practicum.interaction.api.feign.client.WarehouseFeignClient;

@Configuration
@EnableFeignClients(clients = {
        OrderFeignClient.class,
        WarehouseFeignClient.class})
public class FeignClientConfig {
}

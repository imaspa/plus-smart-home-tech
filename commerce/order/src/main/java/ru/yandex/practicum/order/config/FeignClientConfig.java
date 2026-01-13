package ru.yandex.practicum.order.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.interaction.api.feign.client.CartFeignClient;
import ru.yandex.practicum.interaction.api.feign.client.DeliveryFeignClient;
import ru.yandex.practicum.interaction.api.feign.client.PaymentFeignClient;
import ru.yandex.practicum.interaction.api.feign.client.WarehouseFeignClient;

@Configuration
@EnableFeignClients(clients = {
        CartFeignClient.class,
        DeliveryFeignClient.class,
        PaymentFeignClient.class,
        WarehouseFeignClient.class})
public class FeignClientConfig {
}

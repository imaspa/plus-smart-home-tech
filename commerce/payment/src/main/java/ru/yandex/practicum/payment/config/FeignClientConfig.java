package ru.yandex.practicum.payment.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.interaction.api.feign.client.OrderFeignClient;
import ru.yandex.practicum.interaction.api.feign.client.ShoppingStoreFeignClient;

@Configuration
@EnableFeignClients(clients = {
        OrderFeignClient.class,
        ShoppingStoreFeignClient.class})
public class FeignClientConfig {
}

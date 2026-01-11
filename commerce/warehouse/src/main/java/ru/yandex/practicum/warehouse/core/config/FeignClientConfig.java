package ru.yandex.practicum.warehouse.core.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.interaction.api.feign.client.ShoppingStoreFeignClient;

@Configuration
@EnableFeignClients(clients = {ShoppingStoreFeignClient.class})
public class FeignClientConfig {
}

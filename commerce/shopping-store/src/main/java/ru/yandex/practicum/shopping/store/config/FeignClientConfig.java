package ru.yandex.practicum.shopping.store.config;

import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Configuration;
import ru.yandex.practicum.interaction.api.feign.client.WarehouseFeignClient;

@Configuration
@EnableFeignClients(clients = WarehouseFeignClient.class)
public class FeignClientConfig {
}

package ru.yandex.practicum.interaction.api.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.interaction.api.feign.contract.OrderFeignContract;

@FeignClient(name = "order", path = "/api/v1/order")
public interface OrderFeignClient extends OrderFeignContract {
}

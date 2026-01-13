package ru.yandex.practicum.interaction.api.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.interaction.api.feign.contract.DeliveryFeignContract;

@FeignClient(name = "delivery", path = "/api/v1/delivery")
public interface DeliveryFeignClient extends DeliveryFeignContract {
}

package ru.yandex.practicum.interaction.api.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.interaction.api.feign.contract.CartFeignContract;

@FeignClient(name = "shopping-cart", path = "/api/v1/shopping-cart")
public interface CartFeignClient extends CartFeignContract {
}

package ru.yandex.practicum.interaction.api.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.interaction.api.feign.contract.StoreFeignContract;

@FeignClient(name = "shopping-store", path = "/api/v1/shopping-store")
public interface ShoppingStoreFeignClient extends StoreFeignContract {
}
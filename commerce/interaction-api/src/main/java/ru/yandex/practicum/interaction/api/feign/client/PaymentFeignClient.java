package ru.yandex.practicum.interaction.api.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.interaction.api.feign.contract.PaymentFeignContract;

@FeignClient(name = "payment", path = "/api/v1/payment")
public interface PaymentFeignClient extends PaymentFeignContract {
}

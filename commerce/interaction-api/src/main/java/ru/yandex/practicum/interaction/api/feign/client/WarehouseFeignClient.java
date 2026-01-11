package ru.yandex.practicum.interaction.api.feign.client;

import org.springframework.cloud.openfeign.FeignClient;
import ru.yandex.practicum.interaction.api.feign.contract.WarehouseFeignContract;

@FeignClient(name = "warehouse", path = "/api/v1/warehouse")
public interface WarehouseFeignClient extends WarehouseFeignContract {

}

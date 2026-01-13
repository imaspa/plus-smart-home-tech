package ru.yandex.practicum.delivery.core;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.feign.contract.DeliveryFeignContract;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/delivery")
public class DeliveryController implements DeliveryFeignContract {
    private final DeliveryService deliveryService;

    @Override
    public DeliveryDto createDelivery(@RequestBody @Valid DeliveryDto newDeliveryDto) {
        return deliveryService.createDelivery(newDeliveryDto);
    }

    @Override
    public void emulateSuccessfulDelivery(@RequestBody UUID orderId) {
        deliveryService.emulateSuccessfulDelivery(orderId);
    }

    @Override
    public void emulateItemPickup(@RequestBody UUID orderId) {
        deliveryService.emulateItemPickup(orderId);
    }

    @Override
    public void emulateDeliveryDeclined(@RequestBody UUID orderId) {
        deliveryService.emulateDeliveryDeclined(orderId);
    }

    @Override
    public BigDecimal calculateOrderDeliveryCost(@RequestBody @Valid OrderDto orderDto) {
        return deliveryService.calculateOrderDeliveryCost(orderDto);
    }
}

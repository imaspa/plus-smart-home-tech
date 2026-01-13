package ru.yandex.practicum.order.core;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.dto.order.ProductReturnRequest;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/order")
public class OrderController {
    private final OrderService orderService;

    public Page<OrderDto> getClientOrders(@RequestParam @NotBlank String username, Pageable pageable) {
        return orderService.getClientOrders(username, pageable);
    }

    public OrderDto createOrder(@RequestBody @Valid CreateNewOrderRequest newOrderRequest) {
        return orderService.createOrder(newOrderRequest);
    }

    public OrderDto returnOrder(@RequestBody @Valid ProductReturnRequest productReturnRequest) {
        return orderService.returnOrder(productReturnRequest);
    }

    public OrderDto payOrder(@RequestBody UUID orderId) {
        return orderService.payOrder(orderId);
    }

    public OrderDto updateOrderStatusAfterPaymentFailure(@RequestBody UUID orderId) {
        return orderService.updateOrderStatusAfterPaymentFailure(orderId);
    }

    public OrderDto deliveryOrder(@RequestBody UUID orderId) {
        return orderService.deliveryOrder(orderId);
    }

    public OrderDto updateOrderStatusToDeliveryFailed(@RequestBody UUID orderId) {
        return orderService.updateOrderStatusToDeliveryFailed(orderId);
    }

    public OrderDto completeOrder(@RequestBody UUID orderId) {
        return orderService.completeOrder(orderId);
    }

    public OrderDto calculateOrderTotal(@RequestBody UUID orderId) {
        return orderService.calculateOrderTotal(orderId);
    }

    public OrderDto calculateDeliveryCost(@RequestBody UUID orderId) {
        return orderService.calculateDeliveryCost(orderId);
    }

    public OrderDto assembleOrder(@RequestBody UUID orderId) {
        return orderService.assembleOrder(orderId);
    }

    public OrderDto updateOrderStatusToAssemblyFailed(@RequestBody UUID orderId) {
        return orderService.updateOrderStatusToAssemblyFailed(orderId);
    }
}

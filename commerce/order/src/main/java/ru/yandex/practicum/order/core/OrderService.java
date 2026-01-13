package ru.yandex.practicum.order.core;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.constant.DeliveryState;
import ru.yandex.practicum.interaction.api.constant.OrderState;
import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.dto.order.ProductReturnRequest;
import ru.yandex.practicum.interaction.api.dto.payment.PaymentDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AssemblyProductsForOrderRequestDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.api.exception.BadRequestException;
import ru.yandex.practicum.interaction.api.exception.DeliveryOperationFailedException;
import ru.yandex.practicum.interaction.api.exception.NotFoundException;
import ru.yandex.practicum.interaction.api.feign.FeignClientWrapper;
import ru.yandex.practicum.interaction.api.feign.client.CartFeignClient;
import ru.yandex.practicum.interaction.api.feign.client.DeliveryFeignClient;
import ru.yandex.practicum.interaction.api.feign.client.PaymentFeignClient;
import ru.yandex.practicum.interaction.api.feign.client.WarehouseFeignClient;
import ru.yandex.practicum.order.core.model.Order;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class OrderService {
    private final OrderRepository repository;
    private final OrderMapper mapper;

    private final CartFeignClient cartClient;
    private final WarehouseFeignClient warehouseClient;
    private final DeliveryFeignClient deliveryClient;
    private final PaymentFeignClient paymentClient;



    @Transactional(readOnly = true)
    public Page<OrderDto> getClientOrders(String username, Pageable pageable) {
        Page<Order> ordersPage = repository.findAllByUsername(username, pageable);
        return mapper.toOrderDtoPage(ordersPage);
    }

    public OrderDto createOrder(CreateNewOrderRequest newOrderRequest) {
        ShoppingCartDto cartDto = newOrderRequest.getShoppingCartDto();

        String username = FeignClientWrapper.call(
                () -> cartClient.getUsernameById(cartDto.getShoppingCartId()),
                String.valueOf(cartDto.getShoppingCartId()),
                "Владелец (пользователь) корнзины"
        );

        BookedProductsDto bookedProductsDto = FeignClientWrapper.call(
                () -> warehouseClient.checkQuantity(cartDto),
                String.valueOf(cartDto.getShoppingCartId()),
                "проверка количества товара"
        );

        AddressDto addressWarehouseDto = FeignClientWrapper.call(
                () -> warehouseClient.getWarehouseAddress(),
                "адрес"
        );

        Order newOrder = mapper.toNewOrder(newOrderRequest, bookedProductsDto, username);
        newOrder = repository.save(newOrder);
        UUID mewOrderId = newOrder.getOrderId();

        DeliveryDto deliveryDto = FeignClientWrapper.call(
                () -> deliveryClient.createDelivery(DeliveryDto.builder()
                        .fromAddress(addressWarehouseDto)
                        .toAddress(newOrderRequest.getDeliveryAddress())
                        .orderId(mewOrderId)
                        .deliveryState(DeliveryState.CREATED)
                        .build()),
                "доставка"
        );
        newOrder.setDeliveryId(deliveryDto.getDeliveryId());
        return mapper.toOrderDto(newOrder);
    }

    public OrderDto returnOrder(ProductReturnRequest productReturnRequest) {
        Order order = getOrderById(productReturnRequest.getOrderId());
        validateReturnProducts(order, productReturnRequest);

        FeignClientWrapper.call(
                warehouseClient::returnProductsToWarehouse,
                order.getProducts(),
                "вернуть товары"
        );
        order.setState(OrderState.PRODUCT_RETURNED);
        return mapper.toOrderDto(order);
    }

    public OrderDto payOrder(UUID orderId) {
        Order order = getOrderById(orderId);

        if (order.getState() == OrderState.PAID) {
            throw new BadRequestException("Заказ уже оплачен");
        }

        if (order.getState() == OrderState.ON_PAYMENT) {
            order.setState(OrderState.PAID);
            return mapper.toOrderDto(order);
        }

        if (!order.getState().equals(OrderState.ASSEMBLED)) {
            throw new BadRequestException("Заказ с ID= %s еще не собран".formatted(orderId));
        }

            order.setState(OrderState.ON_PAYMENT);

        OrderDto dto = mapper.toOrderDto(order);
        PaymentDto paymentDto = FeignClientWrapper.callWithRequest(
                () -> paymentClient.initiatePayment(dto),
                dto,
                "проверка количества товара"
        );
        order.setPaymentId(paymentDto.getPaymentId());
        return mapper.toOrderDto(order);
    }

    public OrderDto updateOrderStatusAfterPaymentFailure(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.PAYMENT_FAILED);
        return mapper.toOrderDto(order);
    }

    public OrderDto deliveryOrder(UUID orderId) {
        Order order = getOrderById(orderId);

        if (order.getState() == OrderState.ON_DELIVERY) {
            order.setState(OrderState.DELIVERED);
            return mapper.toOrderDto(order);
        }

        if (order.getState() != OrderState.PAID) {
            throw new BadRequestException("Заказ не был оплачен");
        }

        try {
            deliveryClient.emulateItemPickup(order.getDeliveryId());
        } catch (FeignException e) {
            throw new DeliveryOperationFailedException(
                    "Не удалось передать заказ в доставку с ID=%s.".formatted(orderId));
        }
        order.setState(OrderState.ON_DELIVERY);
        return mapper.toOrderDto(order);
    }

    public OrderDto updateOrderStatusToDeliveryFailed(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.DELIVERY_FAILED);
        return mapper.toOrderDto(order);
    }

    public OrderDto completeOrder(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.COMPLETED);

        return mapper.toOrderDto(order);
    }

    public OrderDto calculateOrderTotal(UUID orderId) {
        Order order = getOrderById(orderId);
        OrderDto orderDto = mapper.toOrderDto(order);
        BigDecimal productsPrice = FeignClientWrapper.callWithRequest(
                () -> paymentClient.calculateProductsTotal(orderDto),
                orderDto,
                "расчет стоимости продукта"
        );
        BigDecimal totalPrice = FeignClientWrapper.callWithRequest(
                () -> paymentClient.calculateTotalOrderAmount(orderDto),
                orderDto,
                "расчет общей стоимости"
        );
        order.setProductPrice(productsPrice);
        order.setTotalPrice(totalPrice);
        return mapper.toOrderDto(order);
    }

    public OrderDto calculateDeliveryCost(UUID orderId) {
        Order order = getOrderById(orderId);
        OrderDto orderDto = mapper.toOrderDto(order);
        BigDecimal deliveryPryce = FeignClientWrapper.callWithRequest(
                () -> deliveryClient.calculateOrderDeliveryCost(orderDto),
                orderDto,
                "расчет стоимости доставки"
        );
        order.setDeliveryPrice(deliveryPryce);
        return mapper.toOrderDto(order);
    }

    public OrderDto assembleOrder(UUID orderId) {
        Order order = getOrderById(orderId);
        if (order.getState() != OrderState.NEW) {
            throw new BadRequestException("Заказ в статусе не \"NEW\" нельзя отправить на сборку");
        }
        FeignClientWrapper.callWithRequest(
                warehouseClient::assemblyOrderProducts,
                new AssemblyProductsForOrderRequestDto(orderId, order.getProducts()),
                "отправка заказа"
        );
        order.setState(OrderState.ASSEMBLED);
        return mapper.toOrderDto(order);
    }

    public OrderDto updateOrderStatusToAssemblyFailed(UUID orderId) {
        Order order = getOrderById(orderId);
        order.setState(OrderState.ASSEMBLY_FAILED);

        return mapper.toOrderDto(order);
    }


    private Order getOrderById(UUID orderId) {
        return repository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Заказ с ID= %s не найден".formatted(orderId)));
    }

    private void validateReturnProducts(Order order, ProductReturnRequest returnRequest) {
        OrderState orderStateCurrent = order.getState();
        if (orderStateCurrent == OrderState.NEW
                || orderStateCurrent == OrderState.CANCELED
                || orderStateCurrent == OrderState.PRODUCT_RETURNED) {
            throw new BadRequestException("В статусе %s невозможно вернуть заказ".formatted(orderStateCurrent));
        }

        Map<UUID, Long> orderProducts = order.getProducts();
        Map<UUID, Long> returnProducts = returnRequest.getProducts();

        if (orderProducts.equals(returnProducts)) {
            return;
        }

        List<String> missingInReturn = orderProducts.keySet().stream()
                .filter(productId -> !returnProducts.containsKey(productId))
                .map(UUID::toString)
                .collect(Collectors.toList());

        List<String> extraInReturn = returnProducts.keySet().stream()
                .filter(productId -> !orderProducts.containsKey(productId))
                .map(UUID::toString)
                .collect(Collectors.toList());

        List<String> quantityMismatches = orderProducts.entrySet().stream()
                .filter(entry -> {
                    UUID productId = entry.getKey();
                    Long orderedQty = entry.getValue();
                    Long returnQty = returnProducts.get(productId);
                    return returnQty != null && !orderedQty.equals(returnQty);
                })
                .map(entry -> "ID=%s: заказано %d, к возврату %d".formatted(
                        entry.getKey(), entry.getValue(), returnProducts.get(entry.getKey())))
                .collect(Collectors.toList());

        String errorMessage =
                ("Несоответствие списка товаров к возврату. Товаров в заказе: %d, товаров к возврату: %d. " +
                        "Отсутствующие в возврате: %s. Лишние в возврате: %s. Несоответствия количества: %s.").formatted(
                orderProducts.size(), returnProducts.size(),
                missingInReturn.isEmpty() ? "нет" : "[" + String.join(", ", missingInReturn) + "]",
                extraInReturn.isEmpty() ? "нет" : "[" + String.join(", ", extraInReturn) + "]",
                quantityMismatches.isEmpty() ? "нет" : "[" + String.join(", ", quantityMismatches) + "]"
        );

        throw new BadRequestException(errorMessage);
    }
}

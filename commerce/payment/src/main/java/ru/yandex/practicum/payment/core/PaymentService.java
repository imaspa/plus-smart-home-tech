package ru.yandex.practicum.payment.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.constant.PaymentState;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.dto.payment.PaymentDto;
import ru.yandex.practicum.interaction.api.dto.store.ProductDto;
import ru.yandex.practicum.interaction.api.exception.BadRequestException;
import ru.yandex.practicum.interaction.api.exception.NotFoundException;
import ru.yandex.practicum.interaction.api.feign.FeignClientWrapper;
import ru.yandex.practicum.interaction.api.feign.client.OrderFeignClient;
import ru.yandex.practicum.interaction.api.feign.client.ShoppingStoreFeignClient;
import ru.yandex.practicum.interaction.api.utility.AppConstants;
import ru.yandex.practicum.payment.core.model.Payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PaymentService{
    private final PaymentRepository repository;
    private final PaymentMapper mapper;
    private final ShoppingStoreFeignClient shoppingStoreClient;
    private final OrderFeignClient orderClient;

    public PaymentDto initiatePayment(OrderDto orderDto) {
        return mapper.toPaymentDto(repository.save(mapper.toPayment(orderDto)));
    }

    public BigDecimal calculateTotalOrderAmount(OrderDto orderDto) {
        BigDecimal productPrice = Objects.requireNonNullElse(orderDto.getProductPrice(), BigDecimal.ZERO);
        BigDecimal deliveryPrice = Objects.requireNonNullElse(orderDto.getDeliveryPrice(), BigDecimal.ZERO);

        BigDecimal feeTotal = productPrice.multiply(AppConstants.NDS_RATE);
        BigDecimal priceWithFee = productPrice.add(feeTotal);
        BigDecimal totalAmount = priceWithFee.add(deliveryPrice);
        return totalAmount;
    }

    public void emulateSuccessfulPayment(UUID paymentId) {
        Payment payment = getPaymentById(paymentId);
        UUID orderId = payment.getOrderId();
        FeignClientWrapper.callWithRequest(
                orderClient::payOrder,
                orderId,
                "уведомить сервис заказов о успешной оплате"
        );
        payment.setPaymentState(PaymentState.SUCCESS);
    }

    public BigDecimal calculateProductsTotal(OrderDto orderDto) {
        Map<UUID, Long> productsInOrder = orderDto.getProducts();

        if (productsInOrder == null || productsInOrder.isEmpty()) {
            return BigDecimal.ZERO;
        }
        List<UUID> productIds = new ArrayList<>(productsInOrder.keySet());
        List<ProductDto> productInfos = FeignClientWrapper.callWithRequest(
                () -> shoppingStoreClient.getProductsByIds(productIds),
                productIds,
                "получение данные о товарах"
        );

        Map<UUID, ProductDto> productInfoMap = productInfos.stream()
                .collect(Collectors.toMap(ProductDto::getProductId, product -> product));

        BigDecimal totalPrice = BigDecimal.ZERO;
        for (Map.Entry<UUID, Long> orderEntry : productsInOrder.entrySet()) {
            UUID productId = orderEntry.getKey();
            Long quantity = orderEntry.getValue();

            ProductDto productInfo = productInfoMap.get(productId);

            if (productInfo == null || productInfo.getPrice() == null) {
                throw new NotFoundException("Информация о товаре с ID= %s не найдена.".formatted(productId));
            }

            BigDecimal subtotal = productInfo.getPrice().multiply(new BigDecimal(quantity));
            totalPrice = totalPrice.add(subtotal);
        }
        return totalPrice;
    }

    public void emulatePaymentDeclined(UUID paymentId) {
        Payment payment = getPaymentById(paymentId);
        UUID orderId = payment.getOrderId();

        PaymentState currentPaymentState = payment.getPaymentState();
        if (currentPaymentState == PaymentState.FAILED || currentPaymentState == PaymentState.SUCCESS) {
            throw new BadRequestException("Платёж находится на неверном статусе - %s".formatted(currentPaymentState));
        }
        FeignClientWrapper.call(
                () -> orderClient.updateOrderStatusAfterPaymentFailure(orderId),
                String.valueOf(orderId),
                "уведомить сервис заказов о неуспешной оплате"
        );
        payment.setPaymentState(PaymentState.FAILED);
    }

    private Payment getPaymentById(UUID paymentId) {
        return repository.findById(paymentId)
                .orElseThrow(() -> new NotFoundException("Платеж с ID= %s не найден".formatted(paymentId)));
    }
}
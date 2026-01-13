package ru.yandex.practicum.payment.core;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.dto.payment.PaymentDto;
import ru.yandex.practicum.interaction.api.feign.contract.PaymentFeignContract;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/payment")
public class PaymentController implements PaymentFeignContract {
    private final PaymentService paymentService;

    @Override
    public PaymentDto initiatePayment(@RequestBody @Valid OrderDto orderDto) {
        return paymentService.initiatePayment(orderDto);
    }

    @Override
    public BigDecimal calculateTotalOrderAmount(@RequestBody @Valid OrderDto orderDto) {
        return paymentService.calculateTotalOrderAmount(orderDto);
    }

    @Override
    public void emulateSuccessfulPayment(@RequestBody UUID paymentId) {
        paymentService.emulateSuccessfulPayment(paymentId);
    }

    @Override
    public BigDecimal calculateProductsTotal(@RequestBody @Valid OrderDto orderDto) {
        return paymentService.calculateProductsTotal(orderDto);
    }

    @Override
    public void emulatePaymentDeclined(@RequestBody UUID paymentId) {
        paymentService.emulatePaymentDeclined(paymentId);
    }
}

package ru.yandex.practicum.payment.core;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.interaction.api.config.CommonMapperConfiguration;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.dto.payment.PaymentDto;
import ru.yandex.practicum.interaction.api.utility.AppConstants;
import ru.yandex.practicum.payment.core.model.Payment;

@Mapper(config = CommonMapperConfiguration.class, imports = {AppConstants.class})
public interface PaymentMapper {
    PaymentDto toPaymentDto(Payment payment);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "paymentState", constant = "PENDING")
    @Mapping(target = "totalPayment", source = "totalPrice")
    @Mapping(target = "deliveryTotal", source = "deliveryPrice")
    @Mapping(target = "orderId", source = "orderId")
    @Mapping(
            target = "feeTotal",
            expression = "java(orderDto.getProductPrice() != null " +
                         "? orderDto.getProductPrice().multiply(AppConstants.NDS_RATE) : java.math.BigDecimal.ZERO)"
    )
    Payment toPayment(OrderDto orderDto);
}

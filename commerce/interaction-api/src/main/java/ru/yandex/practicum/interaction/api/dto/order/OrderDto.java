package ru.yandex.practicum.interaction.api.dto.order;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.yandex.practicum.interaction.api.constant.OrderState;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderDto {
    @NotNull
    private UUID orderId;

    @NotNull
    private UUID shoppingCartId;

    @NotNull(message = "Список продуктов не может быть null")
    @NotEmpty(message = "Список продуктов не может быть пустым")
    private Map<@NotNull(message = "ID продукта не может быть null") UUID,
            @NotNull(message = "Количество не может быть null")
            @Positive(message = "Количество должно быть положительным") Long> products;

    private UUID paymentId;

    private UUID deliveryId;

    private OrderState state;

    @DecimalMin(value = "0.000")
    private BigDecimal deliveryWeight;

    @DecimalMin(value = "0.000")
    private BigDecimal deliveryVolume;

    private Boolean fragile;

    @DecimalMin(value = "0.00")
    private BigDecimal totalPrice;

    @DecimalMin(value = "0.00")
    private BigDecimal deliveryPrice;

    @DecimalMin(value = "0.00")
    private BigDecimal productPrice;
}

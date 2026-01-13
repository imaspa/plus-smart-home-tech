package ru.yandex.practicum.interaction.api.dto.payment;

import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PaymentDto {
    private UUID paymentId;

    @DecimalMin(value = "0.00")
    private BigDecimal totalPayment;

    @DecimalMin(value = "0.00")
    private BigDecimal deliveryTotal;

    @DecimalMin(value = "0.00")
    private BigDecimal feeTotal;
}

package ru.yandex.practicum.payment.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import ru.yandex.practicum.interaction.api.constant.PaymentState;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
@Builder(toBuilder = true)
@Entity
@AllArgsConstructor
@NoArgsConstructor
public class Payment {
    @Id
    @UuidGenerator
    private UUID paymentId;

    @NotNull
    private UUID orderId;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @NotNull
    @Builder.Default
    private PaymentState paymentState = PaymentState.PENDING;

    @Column(precision = 10, scale = 2)
    @DecimalMin(value = "0.00")
    private BigDecimal totalPayment;

    @Column(precision = 10, scale = 2)
    @DecimalMin(value = "0.00")
    private BigDecimal deliveryTotal;

    @Column(precision = 10, scale = 2)
    @DecimalMin(value = "0.00")
    private BigDecimal feeTotal;
}

package ru.yandex.practicum.interaction.api.dto.warehouse;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShippedToDeliveryRequestDto {
    @NotNull(message = "Order ID не может быть пустым")
    private UUID orderId;

    @NotNull(message = "Delivery ID не может быть пустым")
    private UUID deliveryId;
}

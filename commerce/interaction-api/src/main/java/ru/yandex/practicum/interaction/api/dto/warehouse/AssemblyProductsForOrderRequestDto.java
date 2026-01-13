package ru.yandex.practicum.interaction.api.dto.warehouse;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssemblyProductsForOrderRequestDto {
    @NotNull
    private UUID orderId;

    @NotNull(message = "Список продуктов не может быть null")
    @NotEmpty(message = "Список продуктов не может быть пустым")
    private Map<@NotNull(message = "ID продукта не может быть null") UUID,
            @NotNull(message = "Количество не может быть null")
            @Positive(message = "Количество должно быть положительным") Long> products;
}

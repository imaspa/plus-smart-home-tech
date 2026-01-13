package ru.yandex.practicum.interaction.api.dto.warehouse;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BookedProductsDto {
    @NotNull(message = "Общий вес доставки обязателен")
    @DecimalMin(value = "0.000")
    private BigDecimal deliveryWeight;

    @NotNull(message = "Общие объём доставки обязателен")
    @DecimalMin(value = "0.000")
    private BigDecimal deliveryVolume;

    @NotNull(message = "Наличие хрупких вещей в доставке обязательно к указанию")
    private Boolean fragile;
}

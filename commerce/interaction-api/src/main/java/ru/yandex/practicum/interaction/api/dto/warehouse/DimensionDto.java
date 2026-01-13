package ru.yandex.practicum.interaction.api.dto.warehouse;

import jakarta.persistence.Embeddable;
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
@Embeddable
public class DimensionDto {
    @NotNull(message = "Необходимо указать ширину")
    @DecimalMin(value = "1.000", message = "Значение должно быть более 1")
    private BigDecimal width;

    @NotNull(message = "Необходимо указать высоту")
    @DecimalMin(value = "1.000", message = "Значение должно быть более 1")
    private BigDecimal height;

    @NotNull(message = "Необходимо указать глубину")
    @DecimalMin(value = "1.000", message = "Значение должно быть более 1")
    private BigDecimal depth;
}

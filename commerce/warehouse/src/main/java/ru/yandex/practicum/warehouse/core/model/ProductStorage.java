package ru.yandex.practicum.warehouse.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.yandex.practicum.interaction.api.dto.warehouse.DimensionDto;

import java.util.UUID;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Table(name = "product_storage")
public class ProductStorage {
    @Id
    @Column(name = "product_id", nullable = false, unique = true)
    private UUID productId;

    @Column(name = "fragile")
    private Boolean fragile;

    @Embedded
    private DimensionDto dimensionDto;

    @Column(name = "weight", nullable = false)
    private Double weight;

    @Column(name = "quantity")
    private Long quantity = 0L;
}

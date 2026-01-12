package ru.yandex.practicum.shopping.store.core.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.UuidGenerator;
import ru.yandex.practicum.interaction.api.constant.common.State;
import ru.yandex.practicum.interaction.api.constant.store.ProductCategory;
import ru.yandex.practicum.interaction.api.constant.store.QuantityState;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Setter
@Getter
@Table(name = "product")
public class Product {
    @Id
    @UuidGenerator
    private UUID productId;

    @NotBlank(message = "Наименование товара не может быть пустым")
    @Column(nullable = false)
    private String productName;

    @NotBlank(message = "Описание товара не может быть пустым")
    @Column(nullable = false)
    private String description;

    private String imageSrc;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Необходимо указать остаток товара")
    private QuantityState quantityState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Необходимо указать статус товара")
    private State productState;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @NotNull(message = "Необходимо указать категорию товара")
    private ProductCategory productCategory;

    @Column(nullable = false, precision = 10, scale = 2)
    @DecimalMin(value = "1.00", message = "Минимальная стоимость товара 1 руб 00 коп")
    @NotNull(message = "Необходимо указать цену товара")
    private BigDecimal price;
}

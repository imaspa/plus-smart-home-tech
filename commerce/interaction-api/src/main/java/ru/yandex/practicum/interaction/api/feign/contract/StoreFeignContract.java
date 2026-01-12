package ru.yandex.practicum.interaction.api.feign.contract;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.interaction.api.constant.store.ProductCategory;
import ru.yandex.practicum.interaction.api.constant.store.QuantityState;
import ru.yandex.practicum.interaction.api.dto.store.ProductDto;

import java.util.UUID;

public interface StoreFeignContract {
    @GetMapping
    Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable);

    @PutMapping
    ProductDto createProduct(@RequestBody @Valid ProductDto newProductDto);

    @PostMapping
    ProductDto updateProduct(@RequestBody @Valid ProductDto updateProductDto);

    @PostMapping("/removeProductFromStore")
    Boolean deleteProduct(@RequestBody @NotNull UUID productId);

    @PostMapping("/quantityState")
    Boolean updateQuantityState(@RequestParam @NotNull UUID productId, @RequestParam @NotNull QuantityState quantityState);

    @GetMapping("/{productId}")
    ProductDto getProductById(@PathVariable @NotNull UUID productId);
}

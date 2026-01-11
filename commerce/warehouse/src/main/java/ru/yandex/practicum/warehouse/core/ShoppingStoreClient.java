package ru.yandex.practicum.warehouse.core;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
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
import ru.yandex.practicum.interaction.api.feign.StoreClient;

import java.util.UUID;

@FeignClient(name = "shopping-store", path = "/api/v1")
public interface ShoppingStoreClient extends StoreClient {

    @Override
    @GetMapping("/shopping-store")
    Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable);

    @Override
    @PutMapping("/shopping-store")
    ProductDto createProduct(@RequestBody @Valid ProductDto newProductDto);

    @Override
    @PostMapping("/shopping-store")
    ProductDto updateProduct(@RequestBody @Valid ProductDto updateProductDto);

    @Override
    @PostMapping("/shopping-store/removeProductFromStore")
    Boolean deleteProduct(@RequestBody @NotNull UUID productId);

    @Override
    @PostMapping("/shopping-store/quantityState")
    Boolean updateQuantityState(@RequestParam @NotNull UUID productId,
                                @RequestParam @NotNull QuantityState quantityState);

    @Override
    @GetMapping("/shopping-store/{productId}")
    ProductDto getProductById(@PathVariable @NotNull UUID productId);
}
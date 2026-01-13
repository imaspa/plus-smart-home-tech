package ru.yandex.practicum.shopping.store.core;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.constant.store.ProductCategory;
import ru.yandex.practicum.interaction.api.constant.store.QuantityState;
import ru.yandex.practicum.interaction.api.dto.store.ProductDto;
import ru.yandex.practicum.interaction.api.feign.contract.StoreFeignContract;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/shopping-store")
public class ProductController implements StoreFeignContract {
    private final ProductService productService;

    @Override
    public Page<ProductDto> getProducts(@RequestParam ProductCategory category, Pageable pageable) {
        return productService.getProducts(category, pageable);
    }

    @Override
    public ProductDto createProduct(@Valid @RequestBody ProductDto newProductDto) {
        return productService.createProduct(newProductDto);
    }

    @Override
    public ProductDto updateProduct(@Valid @RequestBody ProductDto updateProductDto) {
        return productService.updateProduct(updateProductDto);
    }

    @Override
    public Boolean deleteProduct(@RequestBody @NotNull UUID productId) {
        return productService.deleteProduct(productId);
    }

    @Override
    public Boolean updateQuantityState(@RequestParam @NotNull UUID productId, @RequestParam @NotNull QuantityState quantityState) {
        return productService.updateQuantityState(productId, quantityState);
    }

    @Override
    public ProductDto getProductById(@PathVariable @NotNull UUID productId) {
        return productService.getProductById(productId);
    }

    @Override
    public List<ProductDto> getProductsByIds(List<UUID> productIds) {
        return productService.getProductsByIds(productIds);
    }
}

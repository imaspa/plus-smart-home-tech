package ru.yandex.practicum.shopping.store.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.constant.common.State;
import ru.yandex.practicum.interaction.api.constant.store.ProductCategory;
import ru.yandex.practicum.interaction.api.constant.store.QuantityState;
import ru.yandex.practicum.interaction.api.dto.store.ProductDto;
import ru.yandex.practicum.interaction.api.exception.NotFoundException;
import ru.yandex.practicum.shopping.store.core.model.Product;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductService {
    private final StoreRepository repository;
    private final ProductMapper mapper;

    @Transactional(readOnly = true)
    public Page<ProductDto> getProducts(ProductCategory category, Pageable pageable) {
        Page<Product> products = repository.findAllByProductCategory(category, pageable);
        return mapper.toDtoPage(products);
    }

    @Transactional
    public ProductDto createProduct(ProductDto newProductDto) {
        Product product = mapper.toEntity(newProductDto);
        return mapper.toDto(repository.save(product));
    }

    @Transactional
    public ProductDto updateProduct(ProductDto updateProductDto) {
        Product product = checkProductExist(updateProductDto.getProductId());
        mapper.updateFromDto(updateProductDto, product);
        return mapper.toDto(repository.save(product));
    }

    @Transactional
    public Boolean deleteProduct(UUID productId) {
        Product product = checkProductExist(productId);
        product.setProductState(State.DEACTIVATE);
        repository.save(product);
        return true;
    }

    @Transactional
    public Boolean updateQuantityState(UUID productId, QuantityState quantityState) {
        Product product = checkProductExist(productId);
        product.setQuantityState(quantityState);
        repository.save(product);
        return true;
    }

    @Transactional(readOnly = true)
    public ProductDto getProductById(UUID productId) {
        Product product = checkProductExist(productId);
        return mapper.toDto(product);
    }

    private Product checkProductExist(UUID productId) {
        return repository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Товар c id = %s не найден".formatted(productId)));
    }

    @Transactional(readOnly = true)
    public List<ProductDto> getProductsByIds(List<UUID> productIds) {
        if (productIds == null || productIds.isEmpty()) {
            return Collections.emptyList();
        }
        List<Product> products = repository.findAllById(productIds);

        return mapper.toDtoList(products);
    }
}

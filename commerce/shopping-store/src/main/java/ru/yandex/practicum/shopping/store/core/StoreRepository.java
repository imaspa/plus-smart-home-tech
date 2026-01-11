package ru.yandex.practicum.shopping.store.core;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.interaction.api.constant.store.ProductCategory;
import ru.yandex.practicum.shopping.store.core.model.Product;

import java.util.UUID;

public interface StoreRepository extends JpaRepository<Product, UUID> {
    Page<Product> findAllByProductCategory(ProductCategory productCategory, Pageable pageable);
}

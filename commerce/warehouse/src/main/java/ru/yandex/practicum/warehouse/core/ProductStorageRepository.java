package ru.yandex.practicum.warehouse.core;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.warehouse.core.model.ProductStorage;

import java.util.UUID;

@Repository
public interface ProductStorageRepository extends JpaRepository<ProductStorage, UUID> {

}

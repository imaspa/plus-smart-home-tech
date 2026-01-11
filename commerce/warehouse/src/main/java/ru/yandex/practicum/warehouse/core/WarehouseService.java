package ru.yandex.practicum.warehouse.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;
import ru.yandex.practicum.interaction.api.exception.NotFoundException;
import ru.yandex.practicum.interaction.api.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.interaction.api.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.warehouse.core.model.ProductStorage;

import java.security.SecureRandom;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseService {
    private final ProductStorageRepository repository;
    private final WarehouseMapper mapper;

    private static final String[] ADDRESSES = new String[]{"ADDRESS_1", "ADDRESS_2"};

    private static final String CURRENT_ADDRESS = ADDRESSES[Random.from(new SecureRandom()).nextInt(0, 1)];

    @Transactional
    public void addProduct(NewProductInWarehouseRequestDto newProductInWarehouseRequestDto) {
        UUID productId = newProductInWarehouseRequestDto.getProductId();
        if (repository.existsById(productId)) {
            throw new SpecifiedProductAlreadyInWarehouseException("Товар с ID = %s уже заведен на склад".formatted(productId));
        }
        ProductStorage productStorage = mapper.toWarehouse(newProductInWarehouseRequestDto);
        repository.save(productStorage);
    }

    @Transactional(readOnly = true)
    public BookedProductsDto checkQuantity(ShoppingCartDto shoppingCartDto) {
        double totalWeight = 0.0;
        double totalVolume = 0.0;
        boolean hasFragileItems = false;

        for (Map.Entry<UUID, Long> productEntry : shoppingCartDto.getProducts().entrySet()) {
            UUID productId = productEntry.getKey();
            Long requestedQuantity = productEntry.getValue();

            ProductStorage productStorage = repository.findById(productId)
                    .orElseThrow(() -> new NotFoundException("Товар с ID = %s не найден на складе".formatted(productId)));

            if (productStorage.getQuantity() < requestedQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Недостаточно товара на складе. Товар ID: %s, запрошено: %d, доступно: %d".formatted(productId, requestedQuantity, productStorage.getQuantity()));
            }

            totalWeight += productStorage.getWeight() * requestedQuantity;
            totalVolume += productStorage.getDimensionDto().getWidth() * productStorage.getDimensionDto().getHeight() *
                           productStorage.getDimensionDto().getDepth() * requestedQuantity;

            if (productStorage.getFragile()) {
                hasFragileItems = true;
            }
        }

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragileItems)
                .build();
    }

    public AddressDto getWarehouseAddress() {
        return AddressDto.builder()
                .country(CURRENT_ADDRESS)
                .city(CURRENT_ADDRESS)
                .street(CURRENT_ADDRESS)
                .house(CURRENT_ADDRESS)
                .flat(CURRENT_ADDRESS)
                .build();
    }

    @Transactional
    public void updateProduct(AddProductToWarehouseRequestDto addProductToWarehouseRequestDto) {
        UUID productId = addProductToWarehouseRequestDto.getProductId();

        ProductStorage productStorage = repository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Товар с ID = %s не найден на складе".formatted(productId)));
        productStorage.setQuantity(productStorage.getQuantity() + addProductToWarehouseRequestDto.getQuantity());
        repository.save(productStorage);
    }
}

package ru.yandex.practicum.warehouse.core;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AssemblyProductsForOrderRequestDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.ShippedToDeliveryRequestDto;
import ru.yandex.practicum.interaction.api.exception.NotFoundException;
import ru.yandex.practicum.interaction.api.exception.ProductInShoppingCartLowQuantityInWarehouse;
import ru.yandex.practicum.interaction.api.exception.SpecifiedProductAlreadyInWarehouseException;
import ru.yandex.practicum.warehouse.core.model.OrderBooking;
import ru.yandex.practicum.warehouse.core.model.ProductStorage;
import ru.yandex.practicum.warehouse.core.repository.OrderBookingRepository;
import ru.yandex.practicum.warehouse.core.repository.ProductStorageRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.yandex.practicum.interaction.api.utility.AppConstants.ADDRESSES;
import static ru.yandex.practicum.interaction.api.utility.AppConstants.SCALE_VOLUME;
import static ru.yandex.practicum.interaction.api.utility.AppConstants.SCALE_WEIGHT;

@Service
@RequiredArgsConstructor
@Slf4j
public class WarehouseService {
    private final ProductStorageRepository repository;
    private final WarehouseMapper mapper;

    private final OrderBookingRepository orderBookingRepository;

    private static final SecureRandom SECURE_RANDOM_FOR_SEED = new SecureRandom();
    private static final Random RANDOM_GENERATOR = new Random(SECURE_RANDOM_FOR_SEED.nextLong());
    private static final String CURRENT_ADDRESS = ADDRESSES[RANDOM_GENERATOR.nextInt(ADDRESSES.length)];

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
        Map<UUID, Long> products = shoppingCartDto.getProducts();
        if (products == null || products.isEmpty()) {
            return BookedProductsDto.builder()
                    .deliveryWeight(BigDecimal.ZERO)
                    .deliveryVolume(BigDecimal.ZERO)
                    .fragile(false)
                    .build();
        }

        Set<UUID> productIds = products.keySet();

        Map<UUID, ProductStorage> productsStorage = getWarehouseProducts(productIds);

        return calculateBookedProducts(products, productsStorage);
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
        ProductStorage productStorage = getWarehouseProduct(productId);
        productStorage.setQuantity(productStorage.getQuantity() + addProductToWarehouseRequestDto.getQuantity());
    }

    public void shippedToDelivery(@Valid ShippedToDeliveryRequestDto shippedToDeliveryRequestDto) {
        OrderBooking orderBooking = getOrderBooking(shippedToDeliveryRequestDto.getOrderId());
        orderBooking.setDeliveryId(shippedToDeliveryRequestDto.getDeliveryId());
    }

    @Transactional
    public void returnProductsToWarehouse(Map<UUID, Long> returnProducts) {
        if (returnProducts.isEmpty()) return;
        Map<UUID, ProductStorage> products = getWarehouseProducts(returnProducts.keySet());

        returnProducts.forEach((productId, quantityToReturn) -> {
            ProductStorage productStorage = products.get(productId);
            if (productStorage == null) {
                throw new NotFoundException("Товар с ID = %s не найден на складе".formatted(productId));
            }
            productStorage.setQuantity(productStorage.getQuantity() + quantityToReturn);
        });
    }

    @Transactional
    public void assemblyOrderProducts(@Valid AssemblyProductsForOrderRequestDto assemblyProductsForOrderRequest) {
        Map<UUID, Long> assemblyProducts = assemblyProductsForOrderRequest.getProducts();
        Set<UUID> productIds = assemblyProducts.keySet();
        Map<UUID, ProductStorage> productsStorage = getWarehouseProducts(productIds);

        calculateBookedProducts(assemblyProducts, productsStorage);

        assemblyProducts.forEach((productId, requestedQuantity) -> {
            ProductStorage productStorage = productsStorage.get(productId);
            long newQuantity = productStorage.getQuantity() - requestedQuantity;
            productStorage.setQuantity(newQuantity);
        });

        OrderBooking orderBooking = OrderBooking.builder()
                .orderId(assemblyProductsForOrderRequest.getOrderId())
                .products(assemblyProducts)
                .build();
        orderBookingRepository.save(orderBooking);
    }


    private OrderBooking getOrderBooking(UUID orderId) {
        return orderBookingRepository.findById(orderId)
                .orElseThrow(() -> new NotFoundException("Бронь заказа с ID = %s не найдена".formatted(orderId)));
    }

    private ProductStorage getWarehouseProduct(UUID productId) {
        return repository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Товар с ID = %s не найден на складе".formatted(productId)));
    }

    private Map<UUID, ProductStorage> getWarehouseProducts(Set<UUID> productIds) {
        return repository.findAllById(productIds)
                .stream().collect(Collectors.toMap(ProductStorage::getProductId, Function.identity()));
    }

    private BookedProductsDto calculateBookedProducts(Map<UUID, Long> products, Map<UUID, ProductStorage> productsStorage) {
        BigDecimal totalWeight = BigDecimal.ZERO;
        BigDecimal totalVolume = BigDecimal.ZERO;
        boolean hasFragileItems = false;

        for (Map.Entry<UUID, Long> productEntry : products.entrySet()) {
            UUID productId = productEntry.getKey();
            Long requestedQuantity = productEntry.getValue();

            ProductStorage productStorage = productsStorage.get(productId);
            if (productStorage == null) {
                throw new NotFoundException("Товар с ID = %s не найден на складе".formatted(productId));
            }

            if (productStorage.getQuantity() < requestedQuantity) {
                throw new ProductInShoppingCartLowQuantityInWarehouse(
                        "Недостаточно товара на складе. Товар ID: %s, запрошено: %d, доступно: %d"
                                .formatted(productId, requestedQuantity, productStorage.getQuantity()));
            }

            BigDecimal productWeight = productStorage.getWeight();
            BigDecimal quantityBD = BigDecimal.valueOf(requestedQuantity);
            BigDecimal currentProductTotalWeight = productWeight.multiply(quantityBD);

            totalWeight = totalWeight.add(currentProductTotalWeight);

            Objects.requireNonNull(productStorage.getDimension(), "Размеры товара с ID %s не заданы".formatted(productId));

            BigDecimal width = productStorage.getDimension().getWidth();
            BigDecimal height = productStorage.getDimension().getHeight();
            BigDecimal depth = productStorage.getDimension().getDepth();

            BigDecimal singleProductVolume = width.multiply(height).multiply(depth);
            BigDecimal currentProductTotalVolume = singleProductVolume.multiply(quantityBD);

            totalVolume = totalVolume.add(currentProductTotalVolume);

            if (Boolean.TRUE.equals(productStorage.getFragile())) {
                hasFragileItems = true;
            }
        }

        totalWeight = totalWeight.setScale(SCALE_WEIGHT, RoundingMode.HALF_UP);
        totalVolume = totalVolume.setScale(SCALE_VOLUME, RoundingMode.HALF_UP);

        return BookedProductsDto.builder()
                .deliveryWeight(totalWeight)
                .deliveryVolume(totalVolume)
                .fragile(hasFragileItems)
                .build();
    }

}

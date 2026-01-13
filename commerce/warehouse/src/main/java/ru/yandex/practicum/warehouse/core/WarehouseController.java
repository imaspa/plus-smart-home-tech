package ru.yandex.practicum.warehouse.core;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AssemblyProductsForOrderRequestDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.ShippedToDeliveryRequestDto;
import ru.yandex.practicum.interaction.api.feign.contract.WarehouseFeignContract;

import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/warehouse")
public class WarehouseController implements WarehouseFeignContract {
    private final WarehouseService warehouseService;

    @Override
    public void addProduct(@RequestBody @Valid NewProductInWarehouseRequestDto newProductInWarehouseRequestDto) {
        warehouseService.addProduct(newProductInWarehouseRequestDto);
    }

    @Override
    public BookedProductsDto checkQuantity(@RequestBody @Valid ShoppingCartDto shoppingCartDto) {
        return warehouseService.checkQuantity(shoppingCartDto);
    }

    @Override
    public void updateProduct(@RequestBody @Valid AddProductToWarehouseRequestDto addProductToWarehouseRequestDto) {
        warehouseService.updateProduct(addProductToWarehouseRequestDto);
    }

    @Override
    public AddressDto getWarehouseAddress() {
        return warehouseService.getWarehouseAddress();
    }

    @Override
    public void shippedToDelivery(ShippedToDeliveryRequestDto shippedToDeliveryRequestDto) {
        warehouseService.shippedToDelivery(shippedToDeliveryRequestDto);
    }

    @Override
    public void returnProductsToWarehouse(Map<@NotNull UUID, @NotNull @Positive Long> returnProducts) {
        warehouseService.returnProductsToWarehouse(returnProducts);
    }

    @Override
    public void assemblyOrderProducts(AssemblyProductsForOrderRequestDto assemblyProductsForOrderRequest) {
        warehouseService.assemblyOrderProducts(assemblyProductsForOrderRequest);
    }
}


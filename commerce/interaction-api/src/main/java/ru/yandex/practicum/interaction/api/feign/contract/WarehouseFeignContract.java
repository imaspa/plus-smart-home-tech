package ru.yandex.practicum.interaction.api.feign.contract;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AssemblyProductsForOrderRequestDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.ShippedToDeliveryRequestDto;

import java.util.Map;
import java.util.UUID;

public interface WarehouseFeignContract {
    @PutMapping
    void addProduct(@RequestBody @Valid NewProductInWarehouseRequestDto newProductInWarehouseRequestDto);

    @PostMapping("/check")
    BookedProductsDto checkQuantity(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    @PostMapping("/add")
    void updateProduct(@RequestBody @Valid AddProductToWarehouseRequestDto addProductToWarehouseRequestDto);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();

    @PostMapping("/shipped")
    void shippedToDelivery(@Valid @RequestBody ShippedToDeliveryRequestDto shippedToDeliveryRequestDto);

    @PostMapping("/return")
    void returnProductsToWarehouse(@RequestBody @NotEmpty Map<@NotNull UUID, @NotNull @Positive Long> returnProducts);

    @PostMapping("/assembly")
    void assemblyOrderProducts(@Valid @RequestBody AssemblyProductsForOrderRequestDto assemblyProductsForOrderRequest);
}

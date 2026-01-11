package ru.yandex.practicum.shopping.cart.core;

import jakarta.validation.Valid;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;

@FeignClient(name = "warehouse", path = "/api/v1")
public interface WarehouseClient extends ru.yandex.practicum.interaction.api.feign.WarehouseClient {

    @Override
    @PutMapping("/warehouse")
    void addProduct(@RequestBody @Valid NewProductInWarehouseRequestDto newProductInWarehouseRequestDto);

    @Override
    @PostMapping("/warehouse/check")
    BookedProductsDto checkQuantity(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    @Override
    @PostMapping("/warehouse/add")
    void updateProduct(@RequestBody AddProductToWarehouseRequestDto addProductToWarehouseRequestDto);

    @Override
    @GetMapping("/warehouse/address")
    AddressDto getWarehouseAddress();
}

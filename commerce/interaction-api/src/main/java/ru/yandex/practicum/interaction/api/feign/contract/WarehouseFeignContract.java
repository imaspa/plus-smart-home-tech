package ru.yandex.practicum.interaction.api.feign.contract;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AddProductToWarehouseRequestDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.AddressDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;

public interface WarehouseFeignContract {
    @PutMapping
    void addProduct(@RequestBody @Valid NewProductInWarehouseRequestDto newProductInWarehouseRequestDto);

    @PostMapping("/check")
    BookedProductsDto checkQuantity(@RequestBody @Valid ShoppingCartDto shoppingCartDto);

    @PostMapping("/add")
    void updateProduct(@RequestBody @Valid AddProductToWarehouseRequestDto addProductToWarehouseRequestDto);

    @GetMapping("/address")
    AddressDto getWarehouseAddress();
}

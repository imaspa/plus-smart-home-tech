package ru.yandex.practicum.shopping.cart.core;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.interaction.api.dto.cart.ChangeProductQuantityRequestDto;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.feign.CartClient;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/api/v1/shopping-cart")
public class CartController implements CartClient {
    private final CartService cartService;

    @Override
    @GetMapping
    public ShoppingCartDto getCart(@NotBlank @RequestParam String username) {
        return cartService.getCart(username);
    }

    @Override
    @PutMapping
    public ShoppingCartDto addProduct(@NotBlank @RequestParam String username, @RequestBody @NotNull Map<UUID, Long> products) {
        return cartService.addProduct(username, products);
    }

    @Override
    @DeleteMapping
    public void deactivateCart(@NotBlank @RequestParam String username) {
        cartService.deactivateCart(username);
    }

    @Override
    @PostMapping("/remove")
    public ShoppingCartDto deleteProduct(@NotBlank @RequestParam String username, @RequestBody @NotEmpty Set<UUID> request) {
        return cartService.deleteProduct(username, request);
    }

    @Override
    @PostMapping("/change-quantity")
    public ShoppingCartDto updateProductQuantity(@NotBlank @RequestParam String username,
                                                 @RequestBody @Valid ChangeProductQuantityRequestDto requestDto) {
        return cartService.updateProductQuantity(username, requestDto);
    }
}

package ru.yandex.practicum.interaction.api.feign;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.interaction.api.dto.cart.ChangeProductQuantityRequestDto;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface CartClient {

    ShoppingCartDto getCart(@NotBlank @RequestParam String username);

    ShoppingCartDto addProduct(@NotBlank @RequestParam String username, @RequestBody @NotNull Map<UUID, Long> products);

    void deactivateCart(@NotBlank @RequestParam String username);

    ShoppingCartDto deleteProduct(@NotBlank @RequestParam String username, @RequestBody @NotEmpty Set<UUID> request);

    public ShoppingCartDto updateProductQuantity(@NotBlank @RequestParam String username,
                                                 @RequestBody @Valid ChangeProductQuantityRequestDto requestDto);
}
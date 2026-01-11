package ru.yandex.practicum.interaction.api.feign.contract;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.yandex.practicum.interaction.api.dto.cart.ChangeProductQuantityRequestDto;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;

import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface CartFeignContract {
    @GetMapping
    ShoppingCartDto getCart(@NotBlank @RequestParam String username);

    @PutMapping
    ShoppingCartDto addProduct(@NotBlank @RequestParam String username, @RequestBody @NotNull Map<UUID, Long> products);

    @DeleteMapping
    void deactivateCart(@NotBlank @RequestParam String username);

    @PostMapping("/remove")
    ShoppingCartDto deleteProduct(@NotBlank @RequestParam String username, @RequestBody @NotEmpty Set<UUID> request);

    @PostMapping("/change-quantity")
    ShoppingCartDto updateProductQuantity(@NotBlank @RequestParam String username,
                                                 @RequestBody @Valid ChangeProductQuantityRequestDto requestDto);
}
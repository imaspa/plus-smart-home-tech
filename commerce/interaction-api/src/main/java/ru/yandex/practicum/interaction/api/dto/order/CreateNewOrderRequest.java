package ru.yandex.practicum.interaction.api.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;
import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Validated
public class CreateNewOrderRequest {
    @NotNull
    @Valid
    private ShoppingCartDto shoppingCartDto;

    @NotNull
    @Valid
    private AddressDto deliveryAddress;
}

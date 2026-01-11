package ru.yandex.practicum.shopping.cart.core;

import org.mapstruct.Mapper;
import ru.yandex.practicum.interaction.api.config.CommonMapperConfiguration;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.shopping.cart.core.model.Cart;

@Mapper(config = CommonMapperConfiguration.class)
public interface CartMapper {
    ShoppingCartDto toDto(Cart cart);
}

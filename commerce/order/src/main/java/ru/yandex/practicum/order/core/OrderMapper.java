package ru.yandex.practicum.order.core;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.yandex.practicum.interaction.api.config.CommonMapperConfiguration;
import ru.yandex.practicum.interaction.api.dto.order.CreateNewOrderRequest;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.order.core.model.Order;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(config = CommonMapperConfiguration.class)
public interface OrderMapper {

    OrderDto toOrderDto(Order order);

    @BeanMapping(ignoreByDefault = true)
    @Mapping(target = "shoppingCartId", source = "request.shoppingCartDto.shoppingCartId")
    @Mapping(target = "products", source = "request.shoppingCartDto.products")
    @Mapping(target = "state", constant = "NEW")
    @Mapping(target = "deliveryWeight", source = "bookedProductsDto.deliveryWeight")
    @Mapping(target = "deliveryVolume", source = "bookedProductsDto.deliveryVolume")
    @Mapping(target = "fragile", source = "bookedProductsDto.fragile")
    @Mapping(target = "username", source = "username")
    Order toNewOrder(CreateNewOrderRequest request, BookedProductsDto bookedProductsDto, String username);

    default Page<OrderDto> toOrderDtoPage(Page<Order> ordersPage) {
        List<OrderDto> dtos = ordersPage.getContent().stream()
                .map(this::toOrderDto)
                .collect(Collectors.toList());
        return new PageImpl<>(dtos, ordersPage.getPageable(), ordersPage.getTotalElements());
    }
}

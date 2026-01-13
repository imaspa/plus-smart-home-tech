package ru.yandex.practicum.delivery.core;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.yandex.practicum.delivery.core.model.Address;
import ru.yandex.practicum.delivery.core.model.Delivery;
import ru.yandex.practicum.interaction.api.config.CommonMapperConfiguration;
import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.delivery.DeliveryDto;

@Mapper(config = CommonMapperConfiguration.class)
public interface DeliveryMapper {
    @Mapping(target = "deliveryId", ignore = true)
    @Mapping(target = "fromAddress", source = "fromAddress", qualifiedByName = "toAddressEntity")
    @Mapping(target = "toAddress", source = "toAddress", qualifiedByName = "toAddressEntity")
    Delivery toDelivery(DeliveryDto dto);

    @Mapping(target = "fromAddress", source = "fromAddress", qualifiedByName = "toAddressDto")
    @Mapping(target = "toAddress", source = "toAddress", qualifiedByName = "toAddressDto")
    DeliveryDto toDeliveryDto(Delivery entity);

    @Named("toAddressEntity")
    @Mapping(target = "addressId", ignore = true)
    Address toAddress(AddressDto dto);

    @Named("toAddressDto")
    AddressDto toAddressDto(Address entity);
}

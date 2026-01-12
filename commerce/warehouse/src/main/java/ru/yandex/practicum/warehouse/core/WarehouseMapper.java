package ru.yandex.practicum.warehouse.core;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.interaction.api.config.CommonMapperConfiguration;
import ru.yandex.practicum.interaction.api.dto.warehouse.NewProductInWarehouseRequestDto;
import ru.yandex.practicum.warehouse.core.model.ProductStorage;

@Mapper(config = CommonMapperConfiguration.class)
public interface WarehouseMapper {
    @Mapping(target = "quantity", constant = "0L")
    ProductStorage toWarehouse(NewProductInWarehouseRequestDto requestDto);

}

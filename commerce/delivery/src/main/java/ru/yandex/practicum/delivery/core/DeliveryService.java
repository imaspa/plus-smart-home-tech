package ru.yandex.practicum.delivery.core;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.delivery.core.model.Address;
import ru.yandex.practicum.delivery.core.model.Delivery;
import ru.yandex.practicum.delivery.core.repository.AddressRepository;
import ru.yandex.practicum.delivery.core.repository.DeliveryRepository;
import ru.yandex.practicum.interaction.api.constant.DeliveryState;
import ru.yandex.practicum.interaction.api.dto.AddressDto;
import ru.yandex.practicum.interaction.api.dto.delivery.DeliveryDto;
import ru.yandex.practicum.interaction.api.dto.order.OrderDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.ShippedToDeliveryRequestDto;
import ru.yandex.practicum.interaction.api.exception.NotFoundException;
import ru.yandex.practicum.interaction.api.feign.FeignClientWrapper;
import ru.yandex.practicum.interaction.api.feign.client.OrderFeignClient;
import ru.yandex.practicum.interaction.api.feign.client.WarehouseFeignClient;
import ru.yandex.practicum.interaction.api.utility.AppConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

import static ru.yandex.practicum.interaction.api.utility.AppConstants.ADDRESSES;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class DeliveryService {
    private final DeliveryRepository repository;
    private final AddressRepository addressRepository;

    private final DeliveryMapper mapper;

    private final OrderFeignClient orderClient;
    private final WarehouseFeignClient warehouseClient;

    public DeliveryDto createDelivery(DeliveryDto newDeliveryDto) {
        Delivery delivery = mapper.toDelivery(newDeliveryDto);

        delivery.setFromAddress(resolveAddress(newDeliveryDto.getFromAddress()));
        delivery.setToAddress(resolveAddress(newDeliveryDto.getToAddress()));

        delivery.setDeliveryState(DeliveryState.CREATED);

        return mapper.toDeliveryDto(repository.save(delivery));
    }

    public void emulateSuccessfulDelivery(UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        FeignClientWrapper.call(
                () -> orderClient.completeOrder(delivery.getOrderId()),
                String.valueOf(delivery.getOrderId()),
                "сформировать заказ"
        );
        delivery.setDeliveryState(DeliveryState.DELIVERED);
    }

    public void emulateItemPickup(UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);

        FeignClientWrapper.callWithRequest(
                warehouseClient::shippedToDelivery,
                new ShippedToDeliveryRequestDto(delivery.getOrderId(), delivery.getDeliveryId()),
                "уведомить склад заказа"
        );
        FeignClientWrapper.call(
                () -> orderClient.assembleOrder(delivery.getOrderId()),
                String.valueOf(delivery.getOrderId()),
                "передать заказ в сборку"
        );
        delivery.setDeliveryState(DeliveryState.IN_PROGRESS);
    }

    public void emulateDeliveryDeclined(UUID orderId) {
        Delivery delivery = getDeliveryByOrderId(orderId);
        FeignClientWrapper.call(
                () -> orderClient.updateOrderStatusToDeliveryFailed(delivery.getOrderId()),
                String.valueOf(delivery.getOrderId()),
                "уведомить о неудачной доставке"
        );
        delivery.setDeliveryState(DeliveryState.FAILED);
    }

    @Transactional(readOnly = true)
    public BigDecimal calculateOrderDeliveryCost(OrderDto orderDto) {
        UUID orderId = orderDto.getOrderId();

        Delivery delivery = getDeliveryByOrderId(orderId);
        String warehouseStreetAddress = FeignClientWrapper.call(
                warehouseClient::getWarehouseAddress,
                "Владелец (пользователь) корнзины"
        ).getStreet();

        String deliveryStreetAddress = (delivery.getToAddress() != null) ? delivery.getToAddress().getStreet() : null;
        BigDecimal totalCost = AppConstants.BASE_DELIVERY_RATE;
        BigDecimal warehouseCharge = BigDecimal.ZERO;

        BigDecimal multiplier = BigDecimal.ONE;

        if (warehouseStreetAddress.contains(ADDRESSES[0])) {
            multiplier = AppConstants.WAREHOUSE_1_ADDRESS_MULTIPLIER;
        } else if (warehouseStreetAddress.contains(ADDRESSES[1])) {
            multiplier = AppConstants.WAREHOUSE_2_ADDRESS_MULTIPLIER;
        }
        warehouseCharge = AppConstants.BASE_DELIVERY_RATE.multiply(multiplier);

        totalCost = totalCost.add(warehouseCharge);

        if (orderDto.getFragile() != null && orderDto.getFragile()) {
            BigDecimal fragileCharge = totalCost.multiply(AppConstants.FRAGILE_MULTIPLIER);
            totalCost = totalCost.add(fragileCharge);
        }

        if (orderDto.getDeliveryWeight() != null) {
            BigDecimal weightCharge = orderDto.getDeliveryWeight().multiply(AppConstants.WEIGHT_MULTIPLIER);
            totalCost = totalCost.add(weightCharge);
        }

        if (orderDto.getDeliveryVolume() != null) {
            BigDecimal volumeCharge = orderDto.getDeliveryVolume().multiply(AppConstants.VOLUME_MULTIPLIER);
            totalCost = totalCost.add(volumeCharge);
        }

        if (deliveryStreetAddress != null && !deliveryStreetAddress.equals(warehouseStreetAddress)) {
            BigDecimal streetCharge = totalCost.multiply(AppConstants.STREET_MULTIPLIER);
            totalCost = totalCost.add(streetCharge);
        } else {
            log.debug("Адрес доставки совпадает со складом или не указан. Надбавка не применяется.");
        }
        return totalCost.setScale(2, RoundingMode.HALF_UP);
    }

    private Delivery getDeliveryByOrderId(UUID orderId) {
        return repository.findByOrderId(orderId)
                .orElseThrow(() -> new NotFoundException("Доставка для заказа с ID= %s не найдена".formatted(orderId)));
    }

    private Address resolveAddress(AddressDto dto) {
        if (dto.getAddressId() != null) {
            return addressRepository.findById(dto.getAddressId())
                    .orElseThrow(() -> new NotFoundException("Адрес не найден: %s".formatted(dto.getAddressId())));
        } else {
            return mapper.toAddress(dto);
        }
    }
}

package ru.yandex.practicum.warehouse.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.warehouse.core.model.OrderBooking;

import java.util.UUID;

public interface OrderBookingRepository extends JpaRepository<OrderBooking, UUID> {
}

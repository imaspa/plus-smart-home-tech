package ru.yandex.practicum.payment.core;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.payment.core.model.Payment;

import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
}

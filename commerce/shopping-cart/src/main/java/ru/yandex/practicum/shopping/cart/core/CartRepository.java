package ru.yandex.practicum.shopping.cart.core;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.yandex.practicum.interaction.api.constant.common.State;
import ru.yandex.practicum.shopping.cart.core.model.Cart;

import java.util.Optional;
import java.util.UUID;

public interface CartRepository extends JpaRepository<Cart, UUID> {
    Optional<Cart> findByUsernameAndStatus(String username, State status);

    Optional<Cart> findByUsername(String username);
}

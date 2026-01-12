package ru.yandex.practicum.shopping.cart.core;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.yandex.practicum.interaction.api.constant.common.State;
import ru.yandex.practicum.interaction.api.dto.cart.ChangeProductQuantityRequestDto;
import ru.yandex.practicum.interaction.api.dto.cart.ShoppingCartDto;
import ru.yandex.practicum.interaction.api.dto.warehouse.BookedProductsDto;
import ru.yandex.practicum.interaction.api.exception.BadRequestException;
import ru.yandex.practicum.interaction.api.exception.NotFoundException;
import ru.yandex.practicum.interaction.api.feign.client.WarehouseFeignClient;
import ru.yandex.practicum.shopping.cart.core.model.Cart;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {
    private final CartRepository repository;
    private final CartMapper mapper;

    private final WarehouseFeignClient warehouseClient;

    @Transactional
    public ShoppingCartDto getCart(String username) {
        Cart cart = repository.findByUsername(username)
                .orElseGet(() ->
                    Cart.builder()
                            .username(username)
                            .status(State.ACTIVE)
                            .products(new HashMap<>())
                            .build()
                );
        return mapper.toDto(cart);
    }

    @Transactional
    public ShoppingCartDto addProduct(String username, Map<UUID, Long> products) {
        if (products.isEmpty()) {
            throw new BadRequestException("Список продуктов для добавления не может быть пустым");
        }
        Cart cart = repository.findByUsername(username)
                .orElseGet(() -> Cart.builder()
                            .username(username)
                            .status(State.ACTIVE)
                            .products(new HashMap<>())
                            .build()
                );

        cart = repository.save(cart);
        updateCartProducts(cart, products);

        try {
            BookedProductsDto bookedProductsDto = warehouseClient.checkQuantity(mapper.toDto(cart));
        } catch (FeignException e) {
            log.error("Ошибка вызова склада: {}", e.getMessage());
            throw new RuntimeException("Склад не доступен", e);
        }
        return mapper.toDto(repository.save(cart));
    }

    @Transactional
    public void deactivateCart(String username) {
        Cart cart = repository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Корзина для пользователя %s не найдена".formatted(username)));

        if (cart.getStatus() != State.DEACTIVATE) {
            cart.setStatus(State.DEACTIVATE);
            repository.save(cart);
        } else {
            log.debug("Корзина уже деактивирована для пользователя: {}", username);
        }
    }

    @Transactional
    public ShoppingCartDto deleteProduct(String username, Set<UUID> request) {
        Cart cart = repository.findByUsernameAndStatus(username, State.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Активной корзины покупок для пользователя %s не найдено".formatted(username)));

        if (cart.getProducts() != null) {
            cart.getProducts().keySet().removeAll(request);
        }

        return mapper.toDto(repository.save(cart));
    }

    @Transactional
    public ShoppingCartDto updateProductQuantity(String username, ChangeProductQuantityRequestDto requestDto) {
        if (requestDto == null) throw new BadRequestException("Запрос на обновление должен быть определен");

        Cart cart = repository.findByUsernameAndStatus(username, State.ACTIVE)
                .orElseThrow(() -> new NotFoundException("Активной корзины покупок для пользователя %s не найдено".formatted(username)));

        UUID productId = requestDto.getProductId();
        Long newQuantity = requestDto.getNewQuantity();

        Map<UUID, Long> products = cart.getProducts();

        if (products == null) {
            products = new HashMap<>();
            cart.setProducts(products);
        }

        if (!products.containsKey(productId)) {
            throw new BadRequestException("Товар с ID %s отсутствует в корзине".formatted(productId));
        }

        if (newQuantity == 0) {
            products.remove(productId);
        } else {
            products.put(productId, newQuantity);
        }

        return mapper.toDto(repository.save(cart));
    }

    private void updateCartProducts(Cart cart, Map<UUID, Long> newProducts) {
        if (cart.getProducts() == null) {
            cart.setProducts(new HashMap<>());
        }
        newProducts.forEach((productId, quantity) ->
                cart.getProducts().merge(productId, quantity, Long::sum));
    }
}

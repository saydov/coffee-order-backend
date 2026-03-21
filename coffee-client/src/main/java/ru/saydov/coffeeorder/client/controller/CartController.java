package ru.saydov.coffeeorder.client.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.saydov.coffeeorder.client.dto.cart.SaveCartRequest;
import ru.saydov.coffeeorder.client.dto.order.OrderItemRequest;
import ru.saydov.coffeeorder.client.service.CartService;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер корзины клиента.
 *
 * <p>Корзина — черновик будущего заказа, хранится в Redis с TTL.
 * Никакой бизнес-валидации (стоп-лист, цены) на этом этапе —
 * проверки выполняются при оформлении заказа.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/client/cart")
public class CartController {

    private final CartService cartService;

    @GetMapping
    public List<OrderItemRequest> get(@AuthenticationPrincipal UUID clientId) {
        return cartService.get(clientId);
    }

    @PutMapping
    public List<OrderItemRequest> save(@AuthenticationPrincipal UUID clientId,
                                       @Valid @RequestBody SaveCartRequest request) {
        cartService.save(clientId, request.getItems());
        return request.getItems();
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void clear(@AuthenticationPrincipal UUID clientId) {
        cartService.clear(clientId);
    }
}

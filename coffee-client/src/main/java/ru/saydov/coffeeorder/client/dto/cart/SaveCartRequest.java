package ru.saydov.coffeeorder.client.dto.cart;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import ru.saydov.coffeeorder.client.dto.order.OrderItemRequest;

import java.util.List;

/**
 * Запрос на сохранение корзины клиента.
 *
 * <p>Принимает полный снапшот позиций — сервис атомарно заменяет старое
 * состояние. Пустой список эквивалентен очистке корзины, но для этого
 * предпочтительнее использовать {@code DELETE /api/client/cart}.
 */
@Getter
@Builder
@ToString
@Jacksonized
public class SaveCartRequest {

    @Valid
    @NotNull
    private final List<OrderItemRequest> items;
}

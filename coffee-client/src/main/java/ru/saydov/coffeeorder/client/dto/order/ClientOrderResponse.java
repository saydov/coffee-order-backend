package ru.saydov.coffeeorder.client.dto.order;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.saydov.coffeeorder.shared.entity.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Краткий ответ на создание заказа.
 *
 * <p>{@code paymentUrl} — {@code null}, если заказ полностью
 * покрыт бонусными баллами и не требует похода в платёжный шлюз.
 * Клиент должен различать эти случаи при отрисовке следующего экрана:
 * редирект на оплату vs сразу на страницу успеха.
 */
@Getter
@Builder
@ToString
public class ClientOrderResponse {

    private final UUID id;

    private final OrderStatus status;

    private final LocalDateTime createdAt;

    private final String paymentUrl;
}

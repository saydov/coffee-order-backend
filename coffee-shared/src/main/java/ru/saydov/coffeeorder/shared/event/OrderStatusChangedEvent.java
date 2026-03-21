package ru.saydov.coffeeorder.shared.event;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.saydov.coffeeorder.shared.entity.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Событие смены статуса заказа.
 *
 * <p>Публикуется {@link ru.saydov.coffeeorder.shared.service.OrderService}
 * при каждом изменении статуса. Используется для трансляции изменений
 * подписчикам через SSE/WebSocket на стороне клиентского приложения.
 *
 * <p>Shared-модуль не зависит от spring-web, поэтому событие — обычный
 * POJO без ссылок на HTTP-инфраструктуру; транспорт реализуется
 * в модулях-подписчиках.
 */
@Getter
@Builder
@ToString
public class OrderStatusChangedEvent {

    private final UUID orderId;

    private final OrderStatus status;

    private final LocalDateTime changedAt;
}

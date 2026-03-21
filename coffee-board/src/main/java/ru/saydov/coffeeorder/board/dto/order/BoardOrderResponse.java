package ru.saydov.coffeeorder.board.dto.order;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.saydov.coffeeorder.shared.entity.order.Order;
import ru.saydov.coffeeorder.shared.entity.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Минимальный ответ с данными заказа для табло.
 *
 * <p>Табло отображает очередь из активных заказов — ему не нужен
 * состав или данные клиента, только id, статус и время создания
 * для сортировки и визуального прогресса.
 */
@Getter
@Builder
@ToString
public class BoardOrderResponse {

    private final UUID id;

    private final OrderStatus status;

    private final LocalDateTime createdAt;

    public static BoardOrderResponse from(Order order) {
        return BoardOrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .build();
    }
}

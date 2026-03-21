package ru.saydov.coffeeorder.admin.dto.order;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.saydov.coffeeorder.shared.entity.order.Order;
import ru.saydov.coffeeorder.shared.entity.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Краткий ответ с данными заказа для админки.
 *
 * <p>В отличие от клиентского {@code ClientOrderDetailResponse}, не
 * раскрывает состав заказа — админке для списочных представлений достаточно
 * количества позиций. Детальный просмотр — отдельный эндпоинт.
 */
@Getter
@Builder
@ToString
public class OrderResponse {

    private final UUID id;

    private final UUID venueId;

    private final OrderStatus status;

    private final LocalDateTime createdAt;

    private final int itemCount;

    public static OrderResponse from(Order order) {
        return OrderResponse.builder()
                .id(order.getId())
                .venueId(order.getVenue().getId())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .itemCount(order.getItems().size())
                .build();
    }
}

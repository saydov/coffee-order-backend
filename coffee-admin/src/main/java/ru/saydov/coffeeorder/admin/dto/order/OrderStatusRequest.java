package ru.saydov.coffeeorder.admin.dto.order;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import ru.saydov.coffeeorder.shared.entity.order.OrderStatus;

/**
 * Запрос на обновление статуса заказа админом.
 *
 * <p>Допустимы любые значения {@link OrderStatus}, включая откат
 * к более раннему — чтобы админ мог исправить ошибочно нажатую кнопку
 * бариста. Валидация перехода бизнес-правилами не реализована намеренно.
 */
@Getter
@Builder
@ToString
@Jacksonized
public class OrderStatusRequest {

    @NotNull
    private final OrderStatus status;
}

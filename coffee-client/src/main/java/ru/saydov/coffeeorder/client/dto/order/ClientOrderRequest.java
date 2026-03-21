package ru.saydov.coffeeorder.client.dto.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.UUID;

/**
 * Запрос на создание заказа.
 *
 * <p>{@code venueId} опционален — если не указан, контроллер попытается
 * определить кофейню из scope-настройки или предпочтения клиента.
 * Отсутствие позиций запрещено; использование бонусов валидируется
 * отдельно в сервисе.
 */
@Getter
@Builder
@ToString
@Jacksonized
public class ClientOrderRequest {

    private final UUID venueId;

    @Valid
    @NotEmpty
    private final List<OrderItemRequest> items;

    @Builder.Default
    @Min(0)
    private final int useBonusPoints = 0;
}

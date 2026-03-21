package ru.saydov.coffeeorder.client.dto.order;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Позиция в запросе на создание заказа или в сохранении корзины.
 *
 * <p>Идентификаторы добавок и карта параметров — защёлкнутые на момент
 * запроса значения; сервис валидирует их против актуального каталога
 * при создании заказа, так что клиент может отправить устаревшие id —
 * он получит осмысленную ошибку.
 */
@Getter
@Builder
@ToString
@Jacksonized
public class OrderItemRequest {

    @NotNull
    private final UUID productId;

    @Min(1)
    private final int quantity;

    @Builder.Default
    private final List<UUID> addonIds = List.of();

    @Builder.Default
    private final Map<String, String> parameters = Map.of();
}

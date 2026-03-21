package ru.saydov.coffeeorder.shared.service;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Данные позиции заказа, передаваемые в сервисный слой.
 *
 * <p>Parameter object для {@link OrderService#create} — изолирует сервис
 * от транспортных DTO разных модулей (клиентского, админского) и содержит
 * только примитивы и коллекции примитивов. Это соблюдает разделение
 * слоёв по стайлу (раздел 40): транспортные типы не пересекают границу.
 *
 * @see OrderService
 */
@Getter
@Builder
@ToString
public class OrderItemInput {

    private final UUID productId;

    private final int quantity;

    @Builder.Default
    private final List<UUID> addonIds = List.of();

    @Builder.Default
    private final Map<String, String> parameters = Map.of();
}

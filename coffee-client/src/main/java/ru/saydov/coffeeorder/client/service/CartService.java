package ru.saydov.coffeeorder.client.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import ru.saydov.coffeeorder.client.dto.order.OrderItemRequest;

import java.time.Duration;
import java.util.List;
import java.util.UUID;

/**
 * Хранилище черновика корзины клиента в Redis.
 *
 * <p>Корзина сохраняется без бизнес-валидации: проверка доступности
 * продуктов, стоп-листа и цен выполняется только при оформлении заказа.
 * TTL обновляется при каждой записи — клиент, регулярно редактирующий
 * корзину, не потеряет её.
 *
 * <p><b>Устойчивость к миграциям:</b> если формат сериализованного JSON
 * изменился (например, добавлено новое поле), {@link #get} очищает битую
 * запись и возвращает пустую корзину вместо падения — клиент просто
 * увидит пустую корзину.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class CartService {

    private static final String KEY_PREFIX = "cart:";
    private static final Duration CART_TTL = Duration.ofDays(7);
    private static final TypeReference<List<OrderItemRequest>> ITEM_LIST_TYPE = new TypeReference<>() {};

    private final StringRedisTemplate redisTemplate;
    private final ObjectMapper objectMapper;

    public List<OrderItemRequest> get(UUID clientId) {
        var json = redisTemplate.opsForValue().get(key(clientId));
        if (json == null) {
            return List.of();
        }
        try {
            return objectMapper.readValue(json, ITEM_LIST_TYPE);
        } catch (JsonProcessingException e) {
            log.warn("Broken cart for client {}, clearing", clientId, e);
            clear(clientId);
            return List.of();
        }
    }

    public void save(UUID clientId, List<OrderItemRequest> items) {
        try {
            var json = objectMapper.writeValueAsString(items);
            redisTemplate.opsForValue().set(key(clientId), json, CART_TTL);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("clientId=" + clientId, e);
        }
    }

    public void clear(UUID clientId) {
        redisTemplate.delete(key(clientId));
    }

    private String key(UUID clientId) {
        return KEY_PREFIX + clientId;
    }
}

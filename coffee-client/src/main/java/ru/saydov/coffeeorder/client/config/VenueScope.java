package ru.saydov.coffeeorder.client.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Инфраструктурный scope, привязывающий инстанс приложения к конкретной кофейне.
 *
 * <p>Задаётся через переменную окружения {@code VENUE_ID} при деплое —
 * позволяет развернуть отдельный инстанс клиентского приложения на планшете
 * в конкретной точке, скрыв выбор кофейни от пользователя. Пустое значение
 * означает multi-venue режим — клиент сам выбирает точку.
 */
@Component
public class VenueScope {

    private final String rawId;

    public VenueScope(@Value("${venue.id:}") String rawId) {
        this.rawId = rawId;
    }

    /**
     * Возвращает настроенный venueId, если инстанс привязан к кофейне.
     *
     * <p>Парсит UUID лениво при каждом вызове — настройка не меняется
     * за время жизни приложения, затраты парсинга пренебрежимо малы
     * по сравнению с остальной логикой обработки запроса.
     *
     * @return UUID кофейни, если привязан; {@link Optional#empty()} в multi-venue режиме
     */
    public Optional<UUID> configured() {
        if (rawId.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(UUID.fromString(rawId));
    }
}

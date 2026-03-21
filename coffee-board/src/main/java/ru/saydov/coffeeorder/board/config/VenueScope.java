package ru.saydov.coffeeorder.board.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

/**
 * Инфраструктурный scope, привязывающий табло к конкретной кофейне.
 *
 * <p>Задаётся через {@code VENUE_ID} при деплое — обычное развёртывание
 * табло на каждую точку с фиксированным идентификатором. Пустое
 * значение включает режим «выбора кофейни» — полезно для dev-стенда.
 *
 * <p>Продублирован из клиентского модуля намеренно: это случайное
 * дублирование (одинаковая логика ≠ общее назначение) — табло и клиент
 * могут разойтись в семантике scope в будущем.
 */
@Component
public class VenueScope {

    private final String rawId;

    public VenueScope(@Value("${venue.id:}") String rawId) {
        this.rawId = rawId;
    }

    public Optional<UUID> configured() {
        if (rawId.isBlank()) {
            return Optional.empty();
        }
        return Optional.of(UUID.fromString(rawId));
    }
}

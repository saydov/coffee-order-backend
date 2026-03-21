package ru.saydov.coffeeorder.admin.dto.stoplist;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

/**
 * Запрос на добавление продукта в стоп-лист кофейни.
 *
 * <p>Идемпотентен — сервис возвращает существующую запись,
 * если продукт уже в стоп-листе, что упрощает логику фронта
 * (не нужно отслеживать текущий набор).
 */
@Getter
@Builder
@ToString
@Jacksonized
public class StopListRequest {

    @NotNull
    private final UUID productId;
}

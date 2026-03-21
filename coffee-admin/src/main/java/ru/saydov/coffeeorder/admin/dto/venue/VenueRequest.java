package ru.saydov.coffeeorder.admin.dto.venue;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * Запрос на создание или обновление кофейни в админке.
 *
 * <p>Один DTO используется для создания и обновления — в отличие от
 * продуктов и категорий, поля кофейни совпадают и риск расхождения
 * минимален (флаг {@code active} доступен обоим операциям).
 * Координаты опциональны — не у всех кофеен есть точная геометка.
 */
@Getter
@Builder
@ToString
@Jacksonized
public class VenueRequest {

    @NotBlank
    private final String name;

    @NotBlank
    private final String address;

    private final boolean active;

    private final Double latitude;

    private final Double longitude;
}

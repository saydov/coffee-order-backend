package ru.saydov.coffeeorder.admin.dto.product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

/**
 * Запрос на создание продукта.
 *
 * <p>Добавки и параметры продукта создаются отдельными эндпоинтами
 * ({@code /api/products/{id}/addons}, {@code /api/products/{id}/parameters}) —
 * это упрощает форму создания и позволяет редактировать их независимо.
 */
@Getter
@Builder
@ToString
@Jacksonized
public class ProductRequest {

    @NotNull
    private final UUID categoryId;

    @NotBlank
    private final String name;

    private final String description;

    @NotNull
    @Positive
    private final Integer price;

    private final String imageUrl;
}

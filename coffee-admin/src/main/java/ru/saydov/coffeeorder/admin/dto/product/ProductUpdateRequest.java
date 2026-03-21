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
 * Запрос на обновление продукта.
 *
 * <p>Отдельный от {@link ProductRequest} DTO — чтобы в будущем
 * добавление обязательных полей только для создания не ломало обновление,
 * и наоборот (раздел style guide «случайное vs истинное дублирование»).
 */
@Getter
@Builder
@ToString
@Jacksonized
public class ProductUpdateRequest {

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

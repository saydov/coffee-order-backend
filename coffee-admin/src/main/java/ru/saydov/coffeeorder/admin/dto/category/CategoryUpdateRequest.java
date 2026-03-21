package ru.saydov.coffeeorder.admin.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * Запрос на обновление категории.
 *
 * <p>Отдельный DTO от {@link CategoryRequest}, даже при совпадении полей —
 * чтобы случайное дублирование в будущем (например, добавление обязательного
 * поля только для создания) не ломало существующий сценарий обновления.
 * См. стайл-гайд, раздел «Случайное vs истинное дублирование».
 */
@Getter
@Builder
@ToString
@Jacksonized
public class CategoryUpdateRequest {

    @NotBlank
    private final String name;

    private final String iconUrl;
}

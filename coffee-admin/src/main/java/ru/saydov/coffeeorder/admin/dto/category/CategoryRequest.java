package ru.saydov.coffeeorder.admin.dto.category;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * Запрос на создание категории каталога.
 *
 * <p>Иконка опциональна — при отсутствии клиент использует дефолтное
 * изображение. Уникальность имени контролируется на уровне БД,
 * поэтому двойной POST с тем же именем вернёт ошибку целостности.
 */
@Getter
@Builder
@ToString
@Jacksonized
public class CategoryRequest {

    @NotBlank
    private final String name;

    private final String iconUrl;
}

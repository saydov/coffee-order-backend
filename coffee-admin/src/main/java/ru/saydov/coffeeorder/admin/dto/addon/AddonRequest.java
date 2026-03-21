package ru.saydov.coffeeorder.admin.dto.addon;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * Запрос на создание добавки к продукту.
 *
 * <p>Цена в рублях — всегда положительная; бесплатная добавка
 * выражается через значение {@code 0} после отдельного согласования,
 * но сейчас такой кейс не поддерживается валидацией.
 */
@Getter
@Builder
@ToString
@Jacksonized
public class AddonRequest {

    @NotBlank
    private final String name;

    @NotNull
    @Positive
    private final Integer price;
}

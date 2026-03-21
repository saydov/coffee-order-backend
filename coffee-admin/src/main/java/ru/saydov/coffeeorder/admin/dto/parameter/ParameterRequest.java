package ru.saydov.coffeeorder.admin.dto.parameter;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import ru.saydov.coffeeorder.shared.entity.ParameterValue;

import java.util.List;

/**
 * Запрос на создание параметра продукта со списком значений.
 *
 * <p>Порядок значений в списке сохраняется — первое значение обычно
 * отображается как выбранное по умолчанию в UI клиента.
 * Конвертация во внутреннюю embedded-сущность делается через
 * {@link #toParameterValues()}, чтобы сервис принимал доменный тип,
 * а не транспортный DTO.
 */
@Getter
@Builder
@ToString
@Jacksonized
public class ParameterRequest {

    @NotBlank
    private final String name;

    @Valid
    @NotEmpty
    private final List<ValueRequest> allowedValues;

    public List<ParameterValue> toParameterValues() {
        return allowedValues.stream()
                .map(v -> ParameterValue.of(v.getValue(), v.getPriceMod()))
                .toList();
    }

    /**
     * Одно допустимое значение параметра.
     *
     * <p>{@code priceMod} может быть отрицательным — например, скидка
     * за размер «мини». По умолчанию {@code 0} — значение идёт без
     * ценового модификатора.
     */
    @Getter
    @Builder
    @ToString
    @Jacksonized
    public static class ValueRequest {

        @NotBlank
        private final String value;

        @Builder.Default
        private final int priceMod = 0;
    }
}

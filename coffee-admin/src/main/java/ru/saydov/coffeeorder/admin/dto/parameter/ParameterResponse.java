package ru.saydov.coffeeorder.admin.dto.parameter;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.saydov.coffeeorder.shared.entity.ParameterValue;
import ru.saydov.coffeeorder.shared.entity.ProductParameter;

import java.util.List;
import java.util.UUID;

/**
 * Ответ с данными параметра продукта для админки.
 *
 * <p>Несёт полный набор значений с ценовыми модификаторами —
 * админке нужна вся информация для редактирования.
 */
@Getter
@Builder
@ToString
public class ParameterResponse {

    private final UUID id;

    private final String name;

    private final List<ValueResponse> allowedValues;

    public static ParameterResponse from(ProductParameter parameter) {
        return ParameterResponse.builder()
                .id(parameter.getId())
                .name(parameter.getName())
                .allowedValues(parameter.getAllowedValues().stream()
                        .map(ValueResponse::from)
                        .toList())
                .build();
    }

    /**
     * Допустимое значение параметра с ценовым модификатором.
     */
    @Getter
    @Builder
    @ToString
    public static class ValueResponse {

        private final String value;

        private final int priceMod;

        public static ValueResponse from(ParameterValue pv) {
            return ValueResponse.builder()
                    .value(pv.getValue())
                    .priceMod(pv.getPriceMod())
                    .build();
        }
    }
}

package ru.saydov.coffeeorder.shared.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Допустимое значение параметра продукта с ценовым модификатором.
 *
 * <p>Embedded-объект, хранящийся в коллекции {@link ProductParameter#getAllowedValues()}.
 * Например, для параметра «Размер» значение «400 мл» может иметь
 * {@code priceMod = 100} — наценка 100 ₽ к базовой цене продукта.
 * Значение без модификатора ({@code priceMod = 0}) — выбор по умолчанию.
 */
@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Embeddable
public class ParameterValue {

    @Column(name = "value", nullable = false)
    private String value;

    @Builder.Default
    @Column(name = "price_mod", nullable = false)
    private int priceMod = 0;

    /**
     * Создаёт значение с указанным ценовым модификатором.
     *
     * <p>Фабрика содержит логику создания — проверка инвариантов
     * и явная семантика через имя метода, — поэтому допустима
     * на конкретном классе по стайлу (раздел 29).
     *
     * @param value     текст значения, отображаемый пользователю
     * @param priceMod  ценовой модификатор в рублях; может быть отрицательным
     * @return новое значение параметра
     */
    public static ParameterValue of(String value, int priceMod) {
        return new ParameterValue(value, priceMod);
    }

    /**
     * Создаёт значение без ценового модификатора.
     *
     * <p>Удобная фабрика для значений по умолчанию — подставляет
     * {@code priceMod = 0}.
     *
     * @param value текст значения, отображаемый пользователю
     * @return новое значение параметра с нулевым модификатором
     */
    public static ParameterValue of(String value) {
        return new ParameterValue(value, 0);
    }
}

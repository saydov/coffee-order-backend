package ru.saydov.coffeeorder.shared.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Параметр продукта — характеристика с набором допустимых значений.
 *
 * <p>Используется для задания вариантов настройки продукта: размер,
 * температура, тип молока. Клиент выбирает одно значение из
 * {@link #allowedValues} при оформлении заказа — каждое значение может
 * нести ценовой модификатор ({@link ParameterValue#getPriceMod()}).
 *
 * @see Product
 * @see ParameterValue
 */
@Getter
@Builder(toBuilder = true)
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "product_parameters")
public class ProductParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private String name;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "product_parameter_values", joinColumns = @JoinColumn(name = "parameter_id"))
    private List<ParameterValue> allowedValues = new ArrayList<>();
}

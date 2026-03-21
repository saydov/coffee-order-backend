package ru.saydov.coffeeorder.shared.entity.order;

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
import jakarta.persistence.MapKeyColumn;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.saydov.coffeeorder.shared.entity.Product;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Позиция в заказе — продукт с количеством и выбранными опциями.
 *
 * <p>Хранит только идентификаторы выбранных добавок и значения
 * параметров — полные объекты ({@link ru.saydov.coffeeorder.shared.entity.ProductAddon},
 * {@link ru.saydov.coffeeorder.shared.entity.ProductParameter})
 * подтягиваются через связь с продуктом. Такой подход упрощает схему БД
 * и сохраняет историю заказов независимо от изменений каталога.
 */
@Getter
@Builder(toBuilder = true)
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private int quantity;

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_item_addons", joinColumns = @JoinColumn(name = "order_item_id"))
    @Column(name = "addon_id")
    private List<UUID> selectedAddonIds = new ArrayList<>();

    @Builder.Default
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "order_item_parameters", joinColumns = @JoinColumn(name = "order_item_id"))
    @MapKeyColumn(name = "parameter_name")
    @Column(name = "parameter_value")
    private Map<String, String> selectedParameters = new HashMap<>();
}

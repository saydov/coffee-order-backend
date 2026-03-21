package ru.saydov.coffeeorder.shared.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

/**
 * Позиция стоп-листа — продукт, временно недоступный для заказа в кофейне.
 *
 * <p>Позволяет админу конкретной точки скрыть продукт из продажи
 * (например, закончились ингредиенты), не удаляя сам продукт из каталога.
 * Уникальность пары {@code (venue_id, product_id)} гарантируется на уровне БД —
 * повторное добавление идемпотентно.
 *
 * @see Venue
 * @see Product
 */
@Getter
@Builder
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "venue_stop_items", uniqueConstraints = @UniqueConstraint(columnNames = {"venue_id", "product_id"}))
public class VenueStopItem {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
}

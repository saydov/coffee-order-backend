package ru.saydov.coffeeorder.shared.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.UUID;

/**
 * Кофейня — физическая точка продаж.
 *
 * <p>Несколько приложений работают с одним списком кофеен: клиентское —
 * для выбора локации при оформлении заказа, админское — для управления
 * каталогом и стоп-листом, табло — для отображения очереди заказов.
 *
 * <p>Неактивные кофейни ({@link #active} == false) скрыты от клиентов,
 * но остаются видимыми в админке для истории заказов.
 */
@Getter
@Builder(toBuilder = true)
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "venues")
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column
    private String city;

    @Builder.Default
    @Column(nullable = false)
    private boolean active = true;

    @Column
    private Double latitude;

    @Column
    private Double longitude;
}

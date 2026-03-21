package ru.saydov.coffeeorder.shared.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Клиент кофейни — пользователь мобильного/веб-приложения.
 *
 * <p>Идентифицируется номером телефона; регистрация неявная — запись создаётся
 * при первой успешной верификации SMS-кода. Копит бонусные баллы, которые
 * можно потратить при оформлении заказа. Предпочитаемая кофейня
 * {@link #preferredVenue} используется для автоподстановки venueId в каталог
 * и заказы, когда клиент явно не указал локацию.
 */
@Getter
@Builder(toBuilder = true)
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "clients")
public class Client {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String phone;

    @Column
    private String name;

    @Builder.Default
    @Column(name = "bonus_points", nullable = false)
    private int bonusPoints = 0;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "preferred_venue_id")
    private Venue preferredVenue;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

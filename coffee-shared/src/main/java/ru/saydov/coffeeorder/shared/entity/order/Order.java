package ru.saydov.coffeeorder.shared.entity.order;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;
import ru.saydov.coffeeorder.shared.entity.Client;
import ru.saydov.coffeeorder.shared.entity.Venue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Заказ клиента в конкретной кофейне.
 *
 * <p>Агрегирует позиции заказа ({@link OrderItem}), текущий статус выполнения,
 * ссылку на клиента (может быть {@code null} для анонимных заказов) и информацию
 * об использованных бонусных баллах. Сумма заказа не хранится в самой сущности —
 * она вычисляется по ценам продуктов, добавок и параметров на момент обработки.
 *
 * <p>Флаг {@link #paymentConfirmed} защищает от повторного списания бонусов
 * при повторной обработке callback от платёжного шлюза.
 */
@Getter
@Builder(toBuilder = true)
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id")
    private Client client;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;

    @Builder.Default
    @Column(name = "bonus_points_used", nullable = false, updatable = false)
    private int bonusPointsUsed = 0;

    @Builder.Default
    @Column(name = "payment_confirmed", nullable = false)
    private boolean paymentConfirmed = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Builder.Default
    @ToString.Exclude
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    /**
     * Проверяет, принадлежит ли заказ указанному клиенту.
     *
     * <p>Инкапсулирует проверку владения, чтобы вызывающий код не доставал
     * клиента из заказа и не обращался к его id (Law of Demeter).
     * Анонимные заказы никому не принадлежат — возвращает {@code false}.
     *
     * @param clientId идентификатор проверяемого клиента
     * @return {@code true} если клиент привязан к заказу
     */
    public boolean isOwnedBy(UUID clientId) {
        return client != null && client.getId().equals(clientId);
    }

    /**
     * Признак анонимного (незарегистрированного) заказа.
     *
     * <p>Анонимные заказы создаются гостями без авторизации; для них недоступны
     * операции с бонусными баллами и просмотр в личном кабинете.
     *
     * @return {@code true} если у заказа нет привязанного клиента
     */
    public boolean isAnonymous() {
        return client == null;
    }

    /**
     * Помечает заказ как оплаченный.
     *
     * <p>Используется после успешного callback от платёжного шлюза.
     * Операция идемпотентна — повторный вызов не меняет состояние,
     * но callers всё равно должны проверять {@link #isPaymentConfirmed()}
     * перед списанием бонусов во избежание двойного дебета.
     */
    public void markPaymentConfirmed() {
        this.paymentConfirmed = true;
    }

    @PrePersist
    private void onCreate() {
        createdAt = LocalDateTime.now();
    }
}

package ru.saydov.coffeeorder.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.saydov.coffeeorder.shared.entity.order.Order;
import ru.saydov.coffeeorder.shared.entity.order.OrderStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий заказов.
 *
 * <p>Выборки с загрузкой позиций используют {@code LEFT JOIN FETCH}
 * для устранения N+1 при последующем расчёте суммы заказа.
 * {@code DISTINCT} нужен из-за картезианского произведения при fetch-join
 * с коллекцией — без него Hibernate вернёт дубликаты parent-сущностей.
 */
public interface OrderRepository extends JpaRepository<Order, UUID> {

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.venue.id = :venueId")
    List<Order> findAllByVenueId(@Param("venueId") UUID venueId);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.venue.id = :venueId AND o.status IN :statuses")
    List<Order> findAllByVenueIdAndStatusIn(@Param("venueId") UUID venueId, @Param("statuses") List<OrderStatus> statuses);

    @Query("SELECT o FROM Order o LEFT JOIN FETCH o.items WHERE o.id = :id")
    Optional<Order> findByIdWithItems(@Param("id") UUID id);

    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.items WHERE o.client.id = :clientId ORDER BY o.createdAt DESC")
    List<Order> findAllByClientId(@Param("clientId") UUID clientId);

    /**
     * Обновляет статус заказа в одном UPDATE-запросе.
     *
     * <p>{@code clearAutomatically = true} — сбрасывает кэш сессии после
     * операции, чтобы последующий {@code findById} в той же транзакции
     * вернул актуальное значение, а не устаревшее из кэша первого уровня.
     *
     * @param id     идентификатор заказа
     * @param status новый статус
     */
    @Modifying(clearAutomatically = true)
    @Query("UPDATE Order o SET o.status = :status WHERE o.id = :id")
    void updateStatus(@Param("id") UUID id, @Param("status") OrderStatus status);
}

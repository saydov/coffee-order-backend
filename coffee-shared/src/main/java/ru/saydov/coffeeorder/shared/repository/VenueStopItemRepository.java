package ru.saydov.coffeeorder.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.saydov.coffeeorder.shared.entity.VenueStopItem;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий позиций стоп-листа.
 *
 * <p>Метод {@link #findStoppedProductIdsByVenueId} возвращает плоский список
 * UUID без загрузки полных {@code VenueStopItem} — оптимизация для частой
 * операции «какие продукты сейчас в стоп-листе».
 */
public interface VenueStopItemRepository extends JpaRepository<VenueStopItem, UUID> {

    List<VenueStopItem> findAllByVenueId(UUID venueId);

    boolean existsByVenueIdAndProductId(UUID venueId, UUID productId);

    void deleteByVenueIdAndProductId(UUID venueId, UUID productId);

    @Query("SELECT si.product.id FROM VenueStopItem si WHERE si.venue.id = :venueId")
    List<UUID> findStoppedProductIdsByVenueId(@Param("venueId") UUID venueId);
}

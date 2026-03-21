package ru.saydov.coffeeorder.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.saydov.coffeeorder.shared.entity.Product;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий продуктов каталога.
 *
 * <p>Ключевой метод — {@link #searchAvailableByVenue}, применяющий каскад
 * опциональных фильтров через один JPQL-запрос. Это избегает условной
 * сборки запроса в сервисном слое и сохраняет выборку предсказуемой
 * для оптимизатора Postgres.
 */
public interface ProductRepository extends JpaRepository<Product, UUID> {

    List<Product> findAllByCategoryId(UUID categoryId);

    /**
     * Поиск доступных в кофейне продуктов с опциональными фильтрами.
     *
     * <p>Исключает позиции из стоп-листа кофейни через подзапрос по
     * {@code VenueStopItem}. Опционально фильтрует по категории
     * и ищет вхождение {@code query} в название или описание
     * (case-insensitive). Любой из параметров {@code categoryId}
     * и {@code query} может быть {@code null} или пустой строкой —
     * в этом случае соответствующий фильтр не применяется.
     *
     * @param venueId    идентификатор кофейни; обязательный
     * @param categoryId идентификатор категории или {@code null}
     * @param query      текст для case-insensitive поиска или {@code null}
     * @return список доступных продуктов; пуст, если ничего не найдено
     */
    @Query("""
            SELECT p FROM Product p
            WHERE p.id NOT IN (
                    SELECT s.product.id FROM VenueStopItem s WHERE s.venue.id = :venueId
                )
              AND (:categoryId IS NULL OR p.category.id = :categoryId)
              AND (
                    :query IS NULL OR :query = ''
                    OR LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%'))
                    OR LOWER(COALESCE(p.description, '')) LIKE LOWER(CONCAT('%', :query, '%'))
                )
            """)
    List<Product> searchAvailableByVenue(@Param("venueId") UUID venueId,
                                         @Param("categoryId") UUID categoryId,
                                         @Param("query") String query);
}

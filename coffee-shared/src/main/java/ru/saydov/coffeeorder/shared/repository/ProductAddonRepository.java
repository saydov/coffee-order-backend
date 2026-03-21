package ru.saydov.coffeeorder.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.saydov.coffeeorder.shared.entity.ProductAddon;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий добавок к продуктам.
 *
 * <p>Добавки хранятся как собственная сущность (не {@code @ElementCollection})
 * чтобы у каждой был стабильный UUID — он используется в позициях заказа
 * ({@code OrderItem.selectedAddonIds}) для ссылки на конкретную добавку.
 */
public interface ProductAddonRepository extends JpaRepository<ProductAddon, UUID> {

    List<ProductAddon> findAllByProductId(UUID productId);
}

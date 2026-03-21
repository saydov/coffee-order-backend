package ru.saydov.coffeeorder.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.saydov.coffeeorder.shared.entity.ProductParameter;

import java.util.List;
import java.util.UUID;

/**
 * Репозиторий параметров продуктов.
 *
 * <p>Параметр — контейнер для набора {@code ParameterValue}, где значения
 * хранятся как {@code @ElementCollection}: у значений нет собственного
 * идентификатора, они ссылаются через имя + родительский параметр.
 */
public interface ProductParameterRepository extends JpaRepository<ProductParameter, UUID> {

    List<ProductParameter> findAllByProductId(UUID productId);
}

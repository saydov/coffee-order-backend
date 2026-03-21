package ru.saydov.coffeeorder.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.saydov.coffeeorder.shared.entity.Category;

import java.util.UUID;

/**
 * Репозиторий категорий каталога.
 *
 * <p>Метод {@code existsByName} используется в админке для предварительной
 * валидации — уникальность также гарантируется на уровне БД.
 */
public interface CategoryRepository extends JpaRepository<Category, UUID> {

    boolean existsByName(String name);
}

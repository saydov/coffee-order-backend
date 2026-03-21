package ru.saydov.coffeeorder.shared.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.saydov.coffeeorder.shared.entity.Category;
import ru.saydov.coffeeorder.shared.exception.CategoryNotFoundException;
import ru.saydov.coffeeorder.shared.repository.CategoryRepository;

import java.util.List;
import java.util.UUID;

/**
 * Сервис управления категориями каталога.
 *
 * <p>Операции CRUD с валидацией существования при удалении.
 * Имя категории уникально — попытка создать дубликат упадёт
 * на уровне БД через unique constraint.
 */
@RequiredArgsConstructor
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional(readOnly = true)
    public List<Category> findAll() {
        return categoryRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Category findById(UUID id) {
        return categoryRepository.findById(id).orElseThrow(() -> new CategoryNotFoundException(id));
    }

    @Transactional
    public Category create(String name) {
        return create(name, null);
    }

    @Transactional
    public Category create(String name, String iconUrl) {
        var category = Category.builder().name(name).iconUrl(iconUrl).build();
        return categoryRepository.save(category);
    }

    @Transactional
    public Category update(UUID id, String name) {
        var existing = findById(id);
        return categoryRepository.save(existing.toBuilder().name(name).build());
    }

    @Transactional
    public void delete(UUID id) {
        if (!categoryRepository.existsById(id)) {
            throw new CategoryNotFoundException(id);
        }
        categoryRepository.deleteById(id);
    }
}

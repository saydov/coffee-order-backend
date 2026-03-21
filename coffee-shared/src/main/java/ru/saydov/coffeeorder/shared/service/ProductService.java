package ru.saydov.coffeeorder.shared.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.saydov.coffeeorder.shared.entity.Product;
import ru.saydov.coffeeorder.shared.exception.ProductNotFoundException;
import ru.saydov.coffeeorder.shared.repository.ProductRepository;

import java.util.List;
import java.util.UUID;

/**
 * Сервис управления каталогом продуктов.
 *
 * <p>Помимо CRUD-операций, отвечает за поиск доступных в конкретной
 * кофейне продуктов — исключая позиции стоп-листа и применяя опциональные
 * фильтры по категории и текстовому запросу.
 */
@RequiredArgsConstructor
@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Transactional(readOnly = true)
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Product> findAllByCategory(UUID categoryId) {
        return productRepository.findAllByCategoryId(categoryId);
    }

    /**
     * Возвращает продукты, доступные для заказа в указанной кофейне.
     *
     * <p>Позиции из стоп-листа кофейни исключаются. Без фильтрации
     * по категории и текстовому запросу — для такой фильтрации используется
     * {@link #searchAvailableByVenue}.
     *
     * @param venueId идентификатор кофейни
     * @return список доступных продуктов; никогда не {@code null}
     */
    @Transactional(readOnly = true)
    public List<Product> findAvailableByVenue(UUID venueId) {
        return searchAvailableByVenue(venueId, null, null);
    }

    /**
     * Поиск продуктов с фильтрами.
     *
     * <p>Обязательный параметр — кофейня; позиции её стоп-листа исключаются.
     * Опциональная фильтрация по категории и case-insensitive вхождению
     * поискового запроса в {@code name} или {@code description} продукта.
     * Пустая строка в {@code query} эквивалентна отсутствию фильтра.
     *
     * @param venueId    идентификатор кофейни
     * @param categoryId идентификатор категории или {@code null} без фильтра
     * @param query      текстовый запрос или {@code null}/blank без фильтра
     * @return список продуктов, подходящих под фильтры
     */
    @Transactional(readOnly = true)
    public List<Product> searchAvailableByVenue(UUID venueId, UUID categoryId, String query) {
        var normalizedQuery = (query == null || query.isBlank()) ? null : query.trim();
        return productRepository.searchAvailableByVenue(venueId, categoryId, normalizedQuery);
    }

    @Transactional(readOnly = true)
    public Product findById(UUID id) {
        return productRepository.findById(id).orElseThrow(() -> new ProductNotFoundException(id));
    }

    @Transactional
    public Product create(UUID categoryId, String name, String description, Integer price, String imageUrl) {
        var category = categoryService.findById(categoryId);
        var product = Product.builder()
                .name(name)
                .description(description)
                .price(price)
                .imageUrl(imageUrl)
                .category(category)
                .build();
        return productRepository.save(product);
    }

    @Transactional
    public Product update(UUID id, UUID categoryId, String name, String description, Integer price, String imageUrl) {
        var existing = findById(id);
        var category = categoryService.findById(categoryId);
        var updated = existing.toBuilder()
                .name(name)
                .description(description)
                .price(price)
                .imageUrl(imageUrl)
                .category(category)
                .build();
        return productRepository.save(updated);
    }

    @Transactional
    public void delete(UUID id) {
        if (!productRepository.existsById(id)) {
            throw new ProductNotFoundException(id);
        }
        productRepository.deleteById(id);
    }
}

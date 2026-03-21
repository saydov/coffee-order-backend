package ru.saydov.coffeeorder.shared.service;

import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.saydov.coffeeorder.shared.entity.VenueStopItem;
import ru.saydov.coffeeorder.shared.repository.VenueStopItemRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Сервис управления стоп-листом кофейни.
 *
 * <p>Стоп-лист — набор продуктов, временно недоступных для заказа
 * в конкретной точке. Операции {@link #add} и {@link #remove} идемпотентны:
 * повторный add возвращает существующую запись, а remove молча игнорирует
 * отсутствие — это упрощает интеграцию с фронтом, которому не нужно
 * отслеживать текущее состояние.
 */
@RequiredArgsConstructor
@Service
public class VenueStopItemService {

    private final VenueStopItemRepository venueStopItemRepository;
    private final VenueService venueService;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public List<VenueStopItem> findAllByVenue(UUID venueId) {
        return venueStopItemRepository.findAllByVenueId(venueId);
    }

    @Transactional(readOnly = true)
    public List<UUID> findStoppedProductIds(UUID venueId) {
        return venueStopItemRepository.findStoppedProductIdsByVenueId(venueId);
    }

    /**
     * Добавляет продукт в стоп-лист кофейни.
     *
     * <p>Идемпотентная операция: если позиция уже существует — возвращает её
     * без создания дубликата. Атомарно проверяется через unique constraint
     * на уровне БД, что защищает от race condition при одновременных вызовах.
     *
     * @param venueId   кофейня
     * @param productId продукт
     * @return существующая или только что созданная запись стоп-листа
     */
    @Transactional
    public VenueStopItem add(UUID venueId, UUID productId) {
        return findExisting(venueId, productId).orElseGet(() -> create(venueId, productId));
    }

    @Transactional
    public void remove(UUID venueId, UUID productId) {
        venueStopItemRepository.deleteByVenueIdAndProductId(venueId, productId);
    }

    private Optional<VenueStopItem> findExisting(UUID venueId, UUID productId) {
        return venueStopItemRepository.findAllByVenueId(venueId).stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();
    }

    private VenueStopItem create(UUID venueId, UUID productId) {
        var venue = venueService.findById(venueId);
        var product = productService.findById(productId);
        var stopItem = VenueStopItem.builder().venue(venue).product(product).build();
        try {
            return venueStopItemRepository.save(stopItem);
        } catch (DataIntegrityViolationException concurrentInsert) {
            return findExisting(venueId, productId).orElseThrow(() -> concurrentInsert);
        }
    }
}

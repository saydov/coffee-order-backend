package ru.saydov.coffeeorder.shared.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.saydov.coffeeorder.shared.entity.Venue;
import ru.saydov.coffeeorder.shared.exception.VenueNotFoundException;
import ru.saydov.coffeeorder.shared.repository.VenueRepository;

import java.util.List;
import java.util.UUID;

/**
 * Сервис управления кофейнями.
 *
 * <p>Разделяет выборки на «все» (для админки и истории заказов)
 * и «только активные» (для клиентского и board-приложений) — неактивные
 * кофейни остаются в БД, но не показываются конечным пользователям.
 */
@RequiredArgsConstructor
@Service
public class VenueService {

    private final VenueRepository venueRepository;

    @Transactional(readOnly = true)
    public List<Venue> findAll() {
        return venueRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Venue> findAllActive() {
        return venueRepository.findAllByActiveTrue();
    }

    @Transactional(readOnly = true)
    public Venue findById(UUID id) {
        return venueRepository.findById(id).orElseThrow(() -> new VenueNotFoundException(id));
    }

    /**
     * Находит кофейню, требуя, чтобы она была активна.
     *
     * <p>Используется там, где попадание в неактивную кофейню недопустимо —
     * создание заказов, выбор предпочитаемой кофейни. Для неактивной
     * бросается {@link VenueNotFoundException}, чтобы не раскрывать
     * клиенту состояние кофейни.
     *
     * @param id идентификатор кофейни
     * @return активная кофейня
     * @throws VenueNotFoundException если кофейня не существует или неактивна
     */
    @Transactional(readOnly = true)
    public Venue findActiveById(UUID id) {
        return venueRepository.findByIdAndActiveTrue(id).orElseThrow(() -> new VenueNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Venue> findActiveByCity(String city) {
        return venueRepository.findAllByActiveTrueAndCityIgnoreCase(city);
    }

    @Transactional(readOnly = true)
    public List<Venue> searchActiveByAddress(String query) {
        return venueRepository.findAllByActiveTrueAndAddressContainingIgnoreCase(query);
    }

    @Transactional
    public Venue create(String name, String address, Double latitude, Double longitude) {
        var venue = Venue.builder()
                .name(name)
                .address(address)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        return venueRepository.save(venue);
    }

    @Transactional
    public Venue update(UUID id, String name, String address, boolean active, Double latitude, Double longitude) {
        var existing = findById(id);
        var updated = existing.toBuilder()
                .name(name)
                .address(address)
                .active(active)
                .latitude(latitude)
                .longitude(longitude)
                .build();
        return venueRepository.save(updated);
    }

    @Transactional
    public void delete(UUID id) {
        if (!venueRepository.existsById(id)) {
            throw new VenueNotFoundException(id);
        }
        venueRepository.deleteById(id);
    }
}

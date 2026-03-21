package ru.saydov.coffeeorder.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.saydov.coffeeorder.shared.entity.Venue;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий кофеен.
 *
 * <p>Выборки разделены на «все» (для админки) и «только активные»
 * (для клиента и табло) — второй вариант исключает кофейни,
 * временно выведенные из эксплуатации.
 */
public interface VenueRepository extends JpaRepository<Venue, UUID> {

    List<Venue> findAllByActiveTrue();

    Optional<Venue> findByIdAndActiveTrue(UUID id);

    List<Venue> findAllByActiveTrueAndCityIgnoreCase(String city);

    List<Venue> findAllByActiveTrueAndAddressContainingIgnoreCase(String query);
}

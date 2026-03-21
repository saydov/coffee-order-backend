package ru.saydov.coffeeorder.admin.dto.venue;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.saydov.coffeeorder.shared.entity.Venue;

import java.util.UUID;

/**
 * Ответ с данными кофейни для админки.
 *
 * <p>В отличие от клиентского {@code ClientVenueResponse} включает флаг
 * {@code active} — админу важно знать состояние кофейни,
 * клиент видит только активные.
 */
@Getter
@Builder
@ToString
public class VenueResponse {

    private final UUID id;

    private final String name;

    private final String address;

    private final boolean active;

    private final Double latitude;

    private final Double longitude;

    public static VenueResponse from(Venue venue) {
        return VenueResponse.builder()
                .id(venue.getId())
                .name(venue.getName())
                .address(venue.getAddress())
                .active(venue.isActive())
                .latitude(venue.getLatitude())
                .longitude(venue.getLongitude())
                .build();
    }
}

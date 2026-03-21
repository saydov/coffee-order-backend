package ru.saydov.coffeeorder.client.dto.venue;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.saydov.coffeeorder.shared.entity.Venue;

import java.util.UUID;

/**
 * Ответ с данными кофейни для клиентского приложения.
 *
 * <p>Координаты опциональны — старые кофейни в БД могут не иметь
 * геометки; клиент должен это учитывать при отрисовке карты.
 */
@Getter
@Builder
@ToString
public class ClientVenueResponse {

    private final UUID id;

    private final String name;

    private final String address;

    private final String city;

    private final Double latitude;

    private final Double longitude;

    public static ClientVenueResponse from(Venue venue) {
        return ClientVenueResponse.builder()
                .id(venue.getId())
                .name(venue.getName())
                .address(venue.getAddress())
                .city(venue.getCity())
                .latitude(venue.getLatitude())
                .longitude(venue.getLongitude())
                .build();
    }
}

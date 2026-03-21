package ru.saydov.coffeeorder.board.dto.venue;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.saydov.coffeeorder.shared.entity.Venue;

import java.util.UUID;

/**
 * Минимальный ответ с данными кофейни для табло.
 *
 * <p>Табло нужны только id и отображаемые название с адресом —
 * координаты и флаг active в интерфейсе не используются.
 */
@Getter
@Builder
@ToString
public class BoardVenueResponse {

    private final UUID id;

    private final String name;

    private final String address;

    public static BoardVenueResponse from(Venue venue) {
        return BoardVenueResponse.builder()
                .id(venue.getId())
                .name(venue.getName())
                .address(venue.getAddress())
                .build();
    }
}

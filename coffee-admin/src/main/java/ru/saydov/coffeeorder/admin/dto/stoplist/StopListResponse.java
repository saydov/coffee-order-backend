package ru.saydov.coffeeorder.admin.dto.stoplist;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.saydov.coffeeorder.shared.entity.VenueStopItem;

import java.util.UUID;

/**
 * Ответ с данными позиции стоп-листа.
 *
 * <p>Дублирует {@code productName} плоско — так админка рисует
 * список стоп-позиций без дополнительного JOIN-запроса на фронте.
 */
@Getter
@Builder
@ToString
public class StopListResponse {

    private final UUID id;

    private final UUID venueId;

    private final UUID productId;

    private final String productName;

    public static StopListResponse from(VenueStopItem stopItem) {
        return StopListResponse.builder()
                .id(stopItem.getId())
                .venueId(stopItem.getVenue().getId())
                .productId(stopItem.getProduct().getId())
                .productName(stopItem.getProduct().getName())
                .build();
    }
}

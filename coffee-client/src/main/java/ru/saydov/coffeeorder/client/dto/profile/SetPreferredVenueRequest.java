package ru.saydov.coffeeorder.client.dto.profile;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.util.UUID;

/**
 * Запрос на установку предпочитаемой кофейни.
 *
 * <p>Идентификатор должен указывать на активную кофейню — для
 * неактивной сервис бросит {@code VenueNotFoundException} и клиент
 * получит 404.
 */
@Getter
@Builder
@ToString
@Jacksonized
public class SetPreferredVenueRequest {

    @NotNull
    private final UUID venueId;
}

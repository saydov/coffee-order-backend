package ru.saydov.coffeeorder.client.dto.profile;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.saydov.coffeeorder.client.dto.venue.ClientVenueResponse;
import ru.saydov.coffeeorder.shared.entity.Client;

import java.util.UUID;

/**
 * Ответ с данными профиля клиента.
 *
 * <p>Помимо базовой информации о клиенте несёт агрегаты по истории
 * заказов — {@code totalOrders} и {@code totalSpent}. Это DTO,
 * специфичное для клиентского приложения; админка и табло видят
 * другие срезы тех же данных.
 */
@Getter
@Builder
@ToString
public class ClientProfileResponse {

    private final UUID id;

    private final String phone;

    private final String name;

    private final int bonusPoints;

    private final ClientVenueResponse preferredVenue;

    private final int totalOrders;

    private final double totalSpent;

    public static ClientProfileResponse from(Client client, int totalOrders, double totalSpent) {
        return ClientProfileResponse.builder()
                .id(client.getId())
                .phone(client.getPhone())
                .name(client.getName())
                .bonusPoints(client.getBonusPoints())
                .preferredVenue(client.getPreferredVenue() != null
                        ? ClientVenueResponse.from(client.getPreferredVenue())
                        : null)
                .totalOrders(totalOrders)
                .totalSpent(totalSpent)
                .build();
    }
}

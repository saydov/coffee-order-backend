package ru.saydov.coffeeorder.admin.dto.addon;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.saydov.coffeeorder.shared.entity.ProductAddon;

import java.util.UUID;

/**
 * Ответ с данными добавки для админки.
 *
 * <p>В отличие от клиентского DTO содержит id — для операций
 * редактирования и удаления в админском интерфейсе.
 */
@Getter
@Builder
@ToString
public class AddonResponse {

    private final UUID id;

    private final String name;

    private final Integer price;

    public static AddonResponse from(ProductAddon addon) {
        return AddonResponse.builder()
                .id(addon.getId())
                .name(addon.getName())
                .price(addon.getPrice())
                .build();
    }
}

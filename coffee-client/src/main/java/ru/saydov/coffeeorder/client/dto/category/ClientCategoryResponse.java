package ru.saydov.coffeeorder.client.dto.category;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.saydov.coffeeorder.shared.entity.Category;

import java.util.UUID;

/**
 * Ответ с данными категории для клиентского приложения.
 *
 * <p>Отдаёт только поля, нужные для отрисовки списка категорий
 * в мобильном/веб-приложении — без служебных полей сущности.
 */
@Getter
@Builder
@ToString
public class ClientCategoryResponse {

    private final UUID id;

    private final String name;

    private final String iconUrl;

    /**
     * Конвертирует entity в DTO.
     *
     * <p>Содержит логику мэппинга между слоями, поэтому фабричный метод
     * допустим на конкретном классе по стайлу (раздел 29).
     *
     * @param category исходная сущность
     * @return DTO категории
     */
    public static ClientCategoryResponse from(Category category) {
        return ClientCategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .iconUrl(category.getIconUrl())
                .build();
    }
}

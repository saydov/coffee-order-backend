package ru.saydov.coffeeorder.admin.dto.category;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.saydov.coffeeorder.shared.entity.Category;

import java.util.UUID;

/**
 * Ответ с данными категории для админки.
 */
@Getter
@Builder
@ToString
public class CategoryResponse {

    private final UUID id;

    private final String name;

    private final String iconUrl;

    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .iconUrl(category.getIconUrl())
                .build();
    }
}

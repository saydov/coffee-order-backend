package ru.saydov.coffeeorder.admin.dto.product;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.saydov.coffeeorder.admin.dto.addon.AddonResponse;
import ru.saydov.coffeeorder.admin.dto.parameter.ParameterResponse;
import ru.saydov.coffeeorder.shared.entity.Product;

import java.util.List;
import java.util.UUID;

/**
 * Ответ с данными продукта для админки.
 *
 * <p>Включает добавки и параметры — админу на странице продукта
 * нужна сразу вся информация для редактирования. Для клиентского
 * приложения есть отдельный DTO {@code ClientProductResponse}
 * с другим набором полей и форматом категории.
 */
@Getter
@Builder
@ToString
public class ProductResponse {

    private final UUID id;

    private final UUID categoryId;

    private final String categoryName;

    private final String name;

    private final String description;

    private final Integer price;

    private final String imageUrl;

    private final List<AddonResponse> addons;

    private final List<ParameterResponse> parameters;

    public static ProductResponse from(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .categoryId(product.getCategory().getId())
                .categoryName(product.getCategory().getName())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .addons(product.getAddons().stream().map(AddonResponse::from).toList())
                .parameters(product.getParameters().stream().map(ParameterResponse::from).toList())
                .build();
    }
}

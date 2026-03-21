package ru.saydov.coffeeorder.client.dto.product;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.saydov.coffeeorder.shared.entity.ParameterValue;
import ru.saydov.coffeeorder.shared.entity.Product;
import ru.saydov.coffeeorder.shared.entity.ProductAddon;
import ru.saydov.coffeeorder.shared.entity.ProductParameter;

import java.util.List;
import java.util.UUID;

/**
 * Ответ с данными продукта для клиентского приложения.
 *
 * <p>Содержит плоский список добавок и список параметров с допустимыми
 * значениями и ценовыми модификаторами. Категория приходит плоско
 * ({@code categoryId} + {@code category}), без вложенного DTO —
 * клиентскому экрану этого достаточно, а экономия на десериализации
 * ощутима на большом каталоге.
 */
@Getter
@Builder
@ToString
public class ClientProductResponse {

    private final UUID id;

    private final String name;

    private final String description;

    private final Integer price;

    private final String imageUrl;

    private final UUID categoryId;

    private final String category;

    private final List<ClientAddonResponse> addons;

    private final List<ClientParameterResponse> parameters;

    public static ClientProductResponse from(Product product) {
        return ClientProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .imageUrl(product.getImageUrl())
                .categoryId(product.getCategory().getId())
                .category(product.getCategory().getName())
                .addons(product.getAddons().stream().map(ClientAddonResponse::from).toList())
                .parameters(product.getParameters().stream().map(ClientParameterResponse::from).toList())
                .build();
    }

    /**
     * Добавка продукта, доступная для выбора клиентом.
     *
     * <p>Содержит id — клиент отправляет его обратно в {@code OrderItemRequest.addonIds}.
     */
    @Getter
    @Builder
    @ToString
    public static class ClientAddonResponse {

        private final UUID id;

        private final String name;

        private final Integer price;

        public static ClientAddonResponse from(ProductAddon addon) {
            return ClientAddonResponse.builder()
                    .id(addon.getId())
                    .name(addon.getName())
                    .price(addon.getPrice())
                    .build();
        }
    }

    /**
     * Параметр продукта с допустимыми значениями для выбора клиентом.
     *
     * <p>Клиент при создании заказа отправляет карту {@code имя → значение},
     * а не {@code id → значение} — это упрощает фронту отображение
     * выбранных значений в корзине.
     */
    @Getter
    @Builder
    @ToString
    public static class ClientParameterResponse {

        private final UUID id;

        private final String name;

        private final List<ClientParameterValueResponse> allowedValues;

        public static ClientParameterResponse from(ProductParameter parameter) {
            return ClientParameterResponse.builder()
                    .id(parameter.getId())
                    .name(parameter.getName())
                    .allowedValues(parameter.getAllowedValues().stream()
                            .map(ClientParameterValueResponse::from)
                            .toList())
                    .build();
        }
    }

    /**
     * Допустимое значение параметра с ценовым модификатором.
     */
    @Getter
    @Builder
    @ToString
    public static class ClientParameterValueResponse {

        private final String value;

        private final int priceMod;

        public static ClientParameterValueResponse from(ParameterValue pv) {
            return ClientParameterValueResponse.builder()
                    .value(pv.getValue())
                    .priceMod(pv.getPriceMod())
                    .build();
        }
    }
}

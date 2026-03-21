package ru.saydov.coffeeorder.client.dto.order;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import ru.saydov.coffeeorder.shared.entity.ParameterValue;
import ru.saydov.coffeeorder.shared.entity.ProductAddon;
import ru.saydov.coffeeorder.shared.entity.ProductParameter;
import ru.saydov.coffeeorder.shared.entity.order.Order;
import ru.saydov.coffeeorder.shared.entity.order.OrderItem;
import ru.saydov.coffeeorder.shared.entity.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Детальный ответ с полной информацией о заказе для истории и экрана заказа.
 *
 * <p>Содержит позиции с расчётом unit-price на момент отрисовки —
 * это копия расчёта из {@code OrderService.calculateItemTotal}, но без умножения
 * на количество. Держать расчёт здесь, а не в сервисе, позволяет DTO быть
 * самодостаточным: вызывающему коду не нужно знать детали ценовой модели.
 */
@Getter
@Builder
@ToString
public class ClientOrderDetailResponse {

    private final UUID id;

    private final OrderStatus status;

    private final LocalDateTime createdAt;

    private final double total;

    private final List<ClientOrderItemResponse> items;

    public static ClientOrderDetailResponse from(Order order, double total) {
        return ClientOrderDetailResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .total(total)
                .items(order.getItems().stream()
                        .map(ClientOrderItemResponse::from)
                        .toList())
                .build();
    }

    /**
     * Позиция заказа — продукт с выбранными опциями и unit-ценой.
     *
     * <p>В отличие от {@link OrderItem} хранит не идентификаторы, а
     * отображаемые данные: название продукта, названия выбранных добавок,
     * значения параметров. Так экран истории остаётся корректным,
     * даже если продукт был переименован после оформления заказа.
     */
    @Getter
    @Builder
    @ToString
    public static class ClientOrderItemResponse {

        private final UUID productId;

        private final String productName;

        private final String productImageUrl;

        private final int quantity;

        private final double unitPrice;

        private final List<ClientOrderAddonResponse> addons;

        private final List<ClientOrderParameterResponse> parameters;

        public static ClientOrderItemResponse from(OrderItem item) {
            var product = item.getProduct();
            var selectedAddonIds = item.getSelectedAddonIds();
            var selectedParams = item.getSelectedParameters();

            var addons = product.getAddons().stream()
                    .filter(a -> selectedAddonIds.contains(a.getId()))
                    .map(ClientOrderAddonResponse::from)
                    .toList();

            var parameters = product.getParameters().stream()
                    .filter(p -> selectedParams.containsKey(p.getName()))
                    .map(p -> ClientOrderParameterResponse.from(p, selectedParams.get(p.getName())))
                    .toList();

            return ClientOrderItemResponse.builder()
                    .productId(product.getId())
                    .productName(product.getName())
                    .productImageUrl(product.getImageUrl())
                    .quantity(item.getQuantity())
                    .unitPrice(calculateUnitPrice(item))
                    .addons(addons)
                    .parameters(parameters)
                    .build();
        }

        private static double calculateUnitPrice(OrderItem item) {
            return item.getProduct().getPrice()
                    + sumSelectedAddons(item)
                    + sumSelectedParameters(item);
        }

        private static int sumSelectedAddons(OrderItem item) {
            return item.getProduct().getAddons().stream()
                    .filter(a -> item.getSelectedAddonIds().contains(a.getId()))
                    .mapToInt(ProductAddon::getPrice)
                    .sum();
        }

        private static int sumSelectedParameters(OrderItem item) {
            return item.getProduct().getParameters().stream()
                    .mapToInt(p -> priceModFor(p, item.getSelectedParameters()))
                    .sum();
        }

        private static int priceModFor(ProductParameter param, Map<String, String> selected) {
            var value = selected.get(param.getName());
            if (value == null) {
                return 0;
            }
            return param.getAllowedValues().stream()
                    .filter(pv -> pv.getValue().equals(value))
                    .findFirst()
                    .map(ParameterValue::getPriceMod)
                    .orElse(0);
        }
    }

    /**
     * Добавка в составе позиции заказа для отображения.
     */
    @Getter
    @Builder
    @ToString
    public static class ClientOrderAddonResponse {

        private final String name;

        private final int price;

        public static ClientOrderAddonResponse from(ProductAddon addon) {
            return ClientOrderAddonResponse.builder()
                    .name(addon.getName())
                    .price(addon.getPrice())
                    .build();
        }
    }

    /**
     * Выбранное значение параметра в составе позиции заказа для отображения.
     */
    @Getter
    @Builder
    @ToString
    public static class ClientOrderParameterResponse {

        private final String name;

        private final String value;

        public static ClientOrderParameterResponse from(ProductParameter param, String selectedValue) {
            return ClientOrderParameterResponse.builder()
                    .name(param.getName())
                    .value(selectedValue)
                    .build();
        }
    }
}

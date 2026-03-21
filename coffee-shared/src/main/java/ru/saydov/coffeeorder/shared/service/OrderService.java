package ru.saydov.coffeeorder.shared.service;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.saydov.coffeeorder.shared.entity.ParameterValue;
import ru.saydov.coffeeorder.shared.entity.ProductAddon;
import ru.saydov.coffeeorder.shared.entity.ProductParameter;
import ru.saydov.coffeeorder.shared.entity.order.Order;
import ru.saydov.coffeeorder.shared.entity.order.OrderItem;
import ru.saydov.coffeeorder.shared.entity.order.OrderStatus;
import ru.saydov.coffeeorder.shared.event.OrderStatusChangedEvent;
import ru.saydov.coffeeorder.shared.exception.InsufficientBonusPointsException;
import ru.saydov.coffeeorder.shared.exception.OrderNotFoundException;
import ru.saydov.coffeeorder.shared.repository.OrderRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Сервис управления заказами — точка входа для создания, обновления статусов
 * и подтверждения оплаты.
 *
 * <p>Инкапсулирует бизнес-правила оформления: расчёт стоимости, валидацию
 * бонусных баллов, идемпотентное подтверждение оплаты и начисление бонусов
 * при переходе заказа в терминальный статус {@link OrderStatus#DONE}.
 * Публикует {@link OrderStatusChangedEvent} при смене статуса — подписчики
 * (SSE-бродкастер) реагируют асинхронно через {@code @EventListener}.
 *
 * <p><b>Начисление бонусов:</b> {@value #BONUS_RATE_PERCENT}% от оплаченной
 * суммы (без учёта списанных бонусов) округлённо до целого рубля.
 */
@RequiredArgsConstructor
@Service
public class OrderService {

    private static final double BONUS_RATE = 0.05;
    private static final int BONUS_RATE_PERCENT = 5;
    private static final List<OrderStatus> ACTIVE_STATUSES =
            List.of(OrderStatus.PENDING, OrderStatus.IN_PROGRESS, OrderStatus.READY);

    private final OrderRepository orderRepository;
    private final VenueService venueService;
    private final ProductService productService;
    private final ClientService clientService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional(readOnly = true)
    public List<Order> findAllByVenue(UUID venueId) {
        return orderRepository.findAllByVenueId(venueId);
    }

    @Transactional(readOnly = true)
    public List<Order> findActiveByVenue(UUID venueId) {
        return orderRepository.findAllByVenueIdAndStatusIn(venueId, ACTIVE_STATUSES);
    }

    @Transactional(readOnly = true)
    public Order findById(UUID id) {
        return orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public Order findByIdWithItems(UUID id) {
        return orderRepository.findByIdWithItems(id).orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public List<Order> findAllByClient(UUID clientId) {
        return orderRepository.findAllByClientId(clientId);
    }

    @Transactional
    public Order create(UUID venueId, List<OrderItemInput> inputs) {
        return create(venueId, null, inputs, 0);
    }

    @Transactional
    public Order create(UUID venueId, UUID clientId, List<OrderItemInput> inputs) {
        return create(venueId, clientId, inputs, 0);
    }

    /**
     * Создаёт заказ с возможностью списания бонусных баллов.
     *
     * <p>Баллы не списываются в момент создания — только валидируются
     * и сохраняются в {@link Order#getBonusPointsUsed()}. Фактическое
     * списание происходит при подтверждении оплаты через
     * {@link #confirmPayment(UUID)} — это защищает от потери баллов,
     * если клиент не завершил платёж.
     *
     * <p>Для анонимных заказов ({@code clientId == null}) использование
     * бонусов запрещено — бросается {@link IllegalArgumentException}.
     *
     * @param venueId         кофейня, в которой оформляется заказ
     * @param clientId        клиент-владелец заказа или {@code null} для анонимного
     * @param inputs          позиции заказа — должны содержать хотя бы одну
     * @param useBonusPoints  количество бонусов к списанию; {@code 0} если не использовать
     * @return сохранённый заказ с присвоенным id
     * @throws InsufficientBonusPointsException если бонусов больше, чем на балансе,
     *                                          или чем итоговая сумма заказа
     * @throws IllegalArgumentException         если бонусы запрошены для анонимного заказа
     *                                          или если {@code useBonusPoints} отрицательный
     */
    @Transactional
    public Order create(UUID venueId, UUID clientId, List<OrderItemInput> inputs, int useBonusPoints) {
        if (useBonusPoints < 0) {
            throw new IllegalArgumentException("useBonusPoints=" + useBonusPoints);
        }
        if (useBonusPoints > 0 && clientId == null) {
            throw new IllegalArgumentException("useBonusPoints=" + useBonusPoints + "; clientId=null");
        }

        var venue = venueService.findActiveById(venueId);
        var builder = Order.builder().venue(venue).bonusPointsUsed(useBonusPoints);

        if (clientId != null) {
            var client = clientService.findById(clientId);
            builder.client(client);

            if (useBonusPoints > client.getBonusPoints()) {
                throw new InsufficientBonusPointsException(
                        "requested=" + useBonusPoints + "; balance=" + client.getBonusPoints());
            }
        }

        var order = builder.build();
        var items = inputs.stream()
                .map(input -> buildOrderItem(order, input))
                .toList();
        order.getItems().addAll(items);

        var total = items.stream().mapToDouble(this::calculateItemTotal).sum();
        if (useBonusPoints > total) {
            throw new InsufficientBonusPointsException(
                    "requested=" + useBonusPoints + "; total=" + total);
        }

        return orderRepository.save(order);
    }

    /**
     * Вычисляет итоговую сумму заказа.
     *
     * <p>Суммирует базовые цены продуктов с учётом количества,
     * выбранных добавок и ценовых модификаторов выбранных значений
     * параметров (размер, молоко и т.д.) по каждой позиции.
     *
     * @param order заказ с загруженными позициями
     * @return итоговая сумма в рублях
     */
    @Transactional(readOnly = true)
    public double calculateTotal(Order order) {
        return order.getItems().stream()
                .mapToDouble(this::calculateItemTotal)
                .sum();
    }

    /**
     * Обновляет статус заказа и при переходе в {@link OrderStatus#DONE}
     * начисляет бонусные баллы клиенту.
     *
     * <p>Бонусы начисляются от оплаченной суммы (total минус списанные бонусы)
     * по ставке {@value #BONUS_RATE_PERCENT}% с округлением до целого рубля.
     * Анонимные заказы бонусов не дают — проверяется через
     * {@link Order#isAnonymous()}.
     *
     * @param orderId идентификатор заказа
     * @param status  новый статус
     * @throws OrderNotFoundException если заказ не найден
     */
    @Transactional
    public void updateStatus(UUID orderId, OrderStatus status) {
        var order = findByIdWithItems(orderId);
        orderRepository.updateStatus(orderId, status);

        if (status == OrderStatus.DONE && !order.isAnonymous()) {
            awardBonusPoints(order);
        }

        eventPublisher.publishEvent(OrderStatusChangedEvent.builder()
                .orderId(orderId)
                .status(status)
                .changedAt(LocalDateTime.now())
                .build());
    }

    /**
     * Подтверждает оплату заказа — списывает использованные бонусные баллы.
     *
     * <p>Вызывается после успешной оплаты в платёжном шлюзе. Идемпотентен —
     * повторный вызов не приведёт к повторному списанию благодаря флагу
     * {@link Order#isPaymentConfirmed()}. Списывается минимум из запрошенного
     * и фактического баланса, что защищает от отрицательного баланса,
     * если клиент потратил бонусы в другом заказе между создавшимся
     * и подтверждением оплаты текущего.
     *
     * @param orderId идентификатор заказа
     * @throws OrderNotFoundException если заказ не найден
     */
    @Transactional
    public void confirmPayment(UUID orderId) {
        var order = findByIdWithItems(orderId);

        if (order.isPaymentConfirmed()) {
            return;
        }

        if (!order.isAnonymous() && order.getBonusPointsUsed() > 0) {
            var client = order.getClient();
            var deduction = Math.min(order.getBonusPointsUsed(), client.getBonusPoints());
            if (deduction > 0) {
                clientService.addBonusPoints(client.getId(), -deduction);
            }
        }

        order.markPaymentConfirmed();
        orderRepository.save(order);
    }

    private void awardBonusPoints(Order order) {
        var total = calculateTotal(order);
        var paidAmount = total - order.getBonusPointsUsed();
        var bonusPoints = (int) Math.round(paidAmount * BONUS_RATE);
        if (bonusPoints > 0) {
            clientService.addBonusPoints(order.getClient().getId(), bonusPoints);
        }
    }

    private double calculateItemTotal(OrderItem item) {
        var basePrice = item.getProduct().getPrice();

        var addonTotal = item.getProduct().getAddons().stream()
                .filter(addon -> item.getSelectedAddonIds().contains(addon.getId()))
                .mapToInt(ProductAddon::getPrice)
                .sum();

        var parameterTotal = item.getProduct().getParameters().stream()
                .mapToInt(param -> calculateParameterPriceMod(param, item.getSelectedParameters()))
                .sum();

        return (basePrice + addonTotal + parameterTotal) * item.getQuantity();
    }

    private int calculateParameterPriceMod(ProductParameter param, Map<String, String> selectedParameters) {
        var selectedValue = selectedParameters.get(param.getName());
        if (selectedValue == null) {
            return 0;
        }
        return param.getAllowedValues().stream()
                .filter(pv -> pv.getValue().equals(selectedValue))
                .findFirst()
                .map(ParameterValue::getPriceMod)
                .orElse(0);
    }

    private OrderItem buildOrderItem(Order order, OrderItemInput input) {
        return OrderItem.builder()
                .order(order)
                .product(productService.findById(input.getProductId()))
                .quantity(input.getQuantity())
                .selectedAddonIds(new ArrayList<>(input.getAddonIds()))
                .selectedParameters(new HashMap<>(input.getParameters()))
                .build();
    }
}

package ru.saydov.coffeeorder.client.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.saydov.coffeeorder.client.config.VenueScope;
import ru.saydov.coffeeorder.client.dto.category.ClientCategoryResponse;
import ru.saydov.coffeeorder.client.dto.order.ClientOrderDetailResponse;
import ru.saydov.coffeeorder.client.dto.order.ClientOrderRequest;
import ru.saydov.coffeeorder.client.dto.order.ClientOrderResponse;
import ru.saydov.coffeeorder.client.dto.payment.CreatePaymentRequest;
import ru.saydov.coffeeorder.client.dto.product.ClientProductResponse;
import ru.saydov.coffeeorder.client.dto.profile.ClientProfileResponse;
import ru.saydov.coffeeorder.client.dto.profile.SetPreferredVenueRequest;
import ru.saydov.coffeeorder.client.dto.profile.UpdateProfileRequest;
import ru.saydov.coffeeorder.client.dto.venue.ClientVenueResponse;
import ru.saydov.coffeeorder.client.service.CartService;
import ru.saydov.coffeeorder.client.service.PaymentClient;
import ru.saydov.coffeeorder.client.util.OrderShortId;
import ru.saydov.coffeeorder.shared.entity.order.Order;
import ru.saydov.coffeeorder.shared.service.CategoryService;
import ru.saydov.coffeeorder.shared.service.ClientService;
import ru.saydov.coffeeorder.shared.service.OrderItemInput;
import ru.saydov.coffeeorder.shared.service.OrderService;
import ru.saydov.coffeeorder.shared.service.ProductService;
import ru.saydov.coffeeorder.shared.service.VenueService;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер клиентского API.
 *
 * <p>Предоставляет эндпоинты для каталога, профиля и заказов.
 * Авторизация — JWT через {@code @AuthenticationPrincipal UUID clientId},
 * который кладёт в контекст {@code JwtAuthFilter}. Часть эндпоинтов
 * каталога открыта без авторизации (см. {@code SecurityConfig}).
 *
 * <p>Контроллер намеренно тонкий — тяжёлая бизнес-логика оформления
 * заказа (расчёт оплачиваемой суммы, интеграция с платёжным шлюзом)
 * делегирована в вспомогательные методы, чтобы тело обработчиков
 * оставалось одного уровня абстракции.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/client")
public class ClientController {

    private static final String CURRENCY_RUB = "RUB";

    private final CategoryService categoryService;
    private final VenueService venueService;
    private final ProductService productService;
    private final OrderService orderService;
    private final ClientService clientService;
    private final CartService cartService;
    private final PaymentClient paymentClient;
    private final VenueScope venueScope;

    @GetMapping("/categories")
    public List<ClientCategoryResponse> getCategories() {
        return categoryService.findAll().stream()
                .map(ClientCategoryResponse::from)
                .toList();
    }

    @GetMapping("/venues")
    public List<ClientVenueResponse> getVenues(@RequestParam(required = false) String city) {
        ensureMultiVenueMode();
        var venues = (city != null && !city.isBlank())
                ? venueService.findActiveByCity(city)
                : venueService.findAllActive();
        return venues.stream()
                .map(ClientVenueResponse::from)
                .toList();
    }

    @GetMapping("/venues/search")
    public List<ClientVenueResponse> searchVenues(@RequestParam String query) {
        ensureMultiVenueMode();
        return venueService.searchActiveByAddress(query).stream()
                .map(ClientVenueResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @GetMapping("/products")
    public List<ClientProductResponse> getProducts(@AuthenticationPrincipal UUID clientId,
                                                   @RequestParam(required = false) UUID venueId,
                                                   @RequestParam(required = false) UUID categoryId,
                                                   @RequestParam(required = false) String q) {
        var effectiveVenueId = resolveVenueId(venueId, clientId);
        return productService.searchAvailableByVenue(effectiveVenueId, categoryId, q).stream()
                .map(ClientProductResponse::from)
                .toList();
    }

    @Transactional(readOnly = true)
    @GetMapping("/profile")
    public ClientProfileResponse getProfile(@AuthenticationPrincipal UUID clientId) {
        return buildProfile(clientService.findById(clientId));
    }

    @PutMapping("/profile")
    public ClientProfileResponse updateProfile(@AuthenticationPrincipal UUID clientId,
                                               @Valid @RequestBody UpdateProfileRequest request) {
        return buildProfile(clientService.updateName(clientId, request.getName()));
    }

    @PutMapping("/profile/venue")
    public ClientProfileResponse setPreferredVenue(@AuthenticationPrincipal UUID clientId,
                                                   @Valid @RequestBody SetPreferredVenueRequest request) {
        return buildProfile(clientService.setPreferredVenue(clientId, request.getVenueId()));
    }

    @Transactional(readOnly = true)
    @GetMapping("/orders")
    public List<ClientOrderDetailResponse> getOrders(@AuthenticationPrincipal UUID clientId) {
        return orderService.findAllByClient(clientId).stream()
                .map(order -> ClientOrderDetailResponse.from(order, orderService.calculateTotal(order)))
                .toList();
    }

    @Transactional(readOnly = true)
    @GetMapping("/orders/{id}")
    public ClientOrderDetailResponse getOrder(@AuthenticationPrincipal UUID clientId,
                                              @PathVariable UUID id) {
        var order = orderService.findByIdWithItems(id);
        if (!order.isOwnedBy(clientId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "orderId=" + id);
        }
        return ClientOrderDetailResponse.from(order, orderService.calculateTotal(order));
    }

    @PostMapping("/orders")
    public ResponseEntity<ClientOrderResponse> createOrder(@AuthenticationPrincipal UUID clientId,
                                                           @Valid @RequestBody ClientOrderRequest request,
                                                           HttpServletRequest httpRequest) {
        var effectiveVenueId = resolveVenueId(request.getVenueId(), clientId);
        var order = orderService.create(effectiveVenueId, clientId, toInputs(request), request.getUseBonusPoints());
        cartService.clear(clientId);

        var amountToPay = orderService.calculateTotal(order) - order.getBonusPointsUsed();
        var response = amountToPay <= 0
                ? finalizeBonusOnlyOrder(order)
                : initiatePayment(order, amountToPay, httpRequest);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Заказ полностью покрыт бонусами — платёжный шлюз не нужен.
     *
     * <p>Сразу подтверждает оплату (списывает бонусы) и возвращает ответ
     * без {@code paymentUrl}. Клиент поймёт, что редирект не требуется.
     */
    private ClientOrderResponse finalizeBonusOnlyOrder(Order order) {
        orderService.confirmPayment(order.getId());
        return ClientOrderResponse.builder()
                .id(order.getId())
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .paymentUrl(null)
                .build();
    }

    private ClientOrderResponse initiatePayment(Order order, double amountToPay, HttpServletRequest httpRequest) {
        var baseUrl = httpRequest.getScheme() + "://" + httpRequest.getHeader("Host");
        var orderId = order.getId();
        var paymentRequest = CreatePaymentRequest.builder()
                .amount(amountToPay)
                .currency(CURRENCY_RUB)
                .orderId(orderId.toString())
                .description("Заказ #" + OrderShortId.of(orderId))
                .redirectUrl(baseUrl + "/payment/result?orderId=" + orderId)
                .build();

        var payment = paymentClient.create(paymentRequest);

        return ClientOrderResponse.builder()
                .id(orderId)
                .status(order.getStatus())
                .createdAt(order.getCreatedAt())
                .paymentUrl(payment.getPaymentUrl())
                .build();
    }

    private List<OrderItemInput> toInputs(ClientOrderRequest request) {
        return request.getItems().stream()
                .map(item -> OrderItemInput.builder()
                        .productId(item.getProductId())
                        .quantity(item.getQuantity())
                        .addonIds(item.getAddonIds())
                        .parameters(item.getParameters())
                        .build())
                .toList();
    }

    /**
     * Собирает профиль клиента с агрегатами по заказам.
     *
     * <p>Вынесено в хелпер чтобы устранить истинное дублирование
     * в трёх обработчиках профиля — {@code get/update/setPreferredVenue}
     * одинаково пересчитывают {@code totalOrders} и {@code totalSpent}.
     *
     * @param client клиент с актуальным состоянием
     * @return DTO профиля с агрегатами
     */
    private ClientProfileResponse buildProfile(ru.saydov.coffeeorder.shared.entity.Client client) {
        var orders = orderService.findAllByClient(client.getId());
        var totalSpent = orders.stream()
                .mapToDouble(orderService::calculateTotal)
                .sum();
        return ClientProfileResponse.from(client, orders.size(), totalSpent);
    }

    /**
     * Определяет эффективный {@code venueId} по приоритету.
     *
     * <p>Порядок источников:
     * <ol>
     *   <li>{@link VenueScope} (env-переменная {@code VENUE_ID}) — инфраструктурный scope</li>
     *   <li>Явно переданный параметр запроса</li>
     *   <li>{@code preferredVenue} авторизованного клиента, если задан</li>
     * </ol>
     *
     * @throws ResponseStatusException {@code 400}, если ни один источник не дал venueId
     */
    private UUID resolveVenueId(UUID requestVenueId, UUID clientId) {
        var scoped = venueScope.configured();
        if (scoped.isPresent()) {
            return scoped.get();
        }
        if (requestVenueId != null) {
            return requestVenueId;
        }
        if (clientId != null) {
            var preferred = clientService.findById(clientId).getPreferredVenue();
            if (preferred != null) {
                return preferred.getId();
            }
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "venueId=null; preferredVenue=null");
    }

    private void ensureMultiVenueMode() {
        if (venueScope.configured().isPresent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "venue-scoped-instance");
        }
    }
}

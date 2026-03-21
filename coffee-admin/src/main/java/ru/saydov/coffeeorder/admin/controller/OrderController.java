package ru.saydov.coffeeorder.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.saydov.coffeeorder.admin.dto.order.OrderResponse;
import ru.saydov.coffeeorder.admin.dto.order.OrderStatusRequest;
import ru.saydov.coffeeorder.shared.service.OrderService;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер просмотра и обновления статуса заказов в админке.
 *
 * <p>Создание и удаление заказов через админку не предусмотрено —
 * эти операции доступны только через клиентское приложение.
 * Админ только меняет статусы и отслеживает состояние.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public List<OrderResponse> findAllByVenue(@RequestParam UUID venueId) {
        return orderService.findAllByVenue(venueId).stream()
                .map(OrderResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public OrderResponse findById(@PathVariable UUID id) {
        return OrderResponse.from(orderService.findById(id));
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Void> updateStatus(@PathVariable UUID id,
                                             @Valid @RequestBody OrderStatusRequest request) {
        orderService.updateStatus(id, request.getStatus());
        return ResponseEntity.noContent().build();
    }
}

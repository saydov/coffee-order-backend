package ru.saydov.coffeeorder.client.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.saydov.coffeeorder.client.security.JwtProvider;
import ru.saydov.coffeeorder.client.service.OrderSseService;
import ru.saydov.coffeeorder.shared.service.OrderService;

import java.util.UUID;

/**
 * SSE-контроллер для трансляции изменений статуса заказа клиенту.
 *
 * <p>Авторизация — через query-параметр {@code token}, так как стандартный
 * браузерный {@code EventSource} не поддерживает кастомные HTTP-заголовки.
 * Валидирует JWT и принадлежность заказа клиенту, чтобы исключить
 * подглядывание за чужими заказами.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/client/orders")
public class OrderSseController {

    private final JwtProvider jwtProvider;
    private final OrderService orderService;
    private final OrderSseService orderSseService;

    @Transactional(readOnly = true)
    @GetMapping(value = "/{id}/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter stream(@PathVariable UUID id, @RequestParam String token) {
        if (!jwtProvider.isValid(token)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "token=invalid");
        }
        var clientId = jwtProvider.getClientId(token);

        var order = orderService.findById(id);
        if (!order.isOwnedBy(clientId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "orderId=" + id);
        }

        return orderSseService.subscribe(id);
    }
}

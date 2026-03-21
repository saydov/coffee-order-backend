package ru.saydov.coffeeorder.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;
import ru.saydov.coffeeorder.board.config.VenueScope;
import ru.saydov.coffeeorder.board.dto.order.BoardOrderResponse;
import ru.saydov.coffeeorder.board.dto.venue.BoardVenueResponse;
import ru.saydov.coffeeorder.shared.service.OrderService;
import ru.saydov.coffeeorder.shared.service.VenueService;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер для табло-приложения.
 *
 * <p>Два режима работы:
 * <ul>
 *   <li><b>venue-scoped</b> — табло привязано к одной кофейне через
 *       {@link VenueScope}. {@code GET /venues} вернёт 403, а {@code GET /orders}
 *       автоматически использует scoped venueId.</li>
 *   <li><b>multi-venue</b> — табло предоставляет список кофеен для выбора;
 *       venueId обязательно передаётся параметром в {@code GET /orders}.</li>
 * </ul>
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/board")
public class BoardController {

    private final OrderService orderService;
    private final VenueService venueService;
    private final VenueScope venueScope;

    @GetMapping("/venues")
    public List<BoardVenueResponse> getVenues() {
        if (venueScope.configured().isPresent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "venue-scoped-instance");
        }
        return venueService.findAllActive().stream()
                .map(BoardVenueResponse::from)
                .toList();
    }

    @GetMapping("/orders")
    public List<BoardOrderResponse> getActiveOrders(@RequestParam(required = false) UUID venueId) {
        var effectiveVenueId = venueScope.configured().orElse(venueId);
        if (effectiveVenueId == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "venueId=null");
        }
        return orderService.findActiveByVenue(effectiveVenueId).stream()
                .map(BoardOrderResponse::from)
                .toList();
    }
}

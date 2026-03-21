package ru.saydov.coffeeorder.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.saydov.coffeeorder.admin.dto.stoplist.StopListRequest;
import ru.saydov.coffeeorder.admin.dto.stoplist.StopListResponse;
import ru.saydov.coffeeorder.shared.service.VenueStopItemService;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер стоп-листа кофейни в админке.
 *
 * <p>Операции идемпотентные — повторный add возвращает существующую
 * запись, повторный remove не считается ошибкой. Это упрощает
 * UI-состояние: админ просто переключает checkbox, а не следит
 * за синхронизацией с сервером.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/venues/{venueId}/stop-list")
public class StopListController {

    private final VenueStopItemService venueStopItemService;

    @GetMapping
    public List<StopListResponse> findAll(@PathVariable UUID venueId) {
        return venueStopItemService.findAllByVenue(venueId).stream()
                .map(StopListResponse::from)
                .toList();
    }

    @PostMapping
    public ResponseEntity<StopListResponse> add(@PathVariable UUID venueId,
                                                @Valid @RequestBody StopListRequest request) {
        var stopItem = venueStopItemService.add(venueId, request.getProductId());
        return ResponseEntity.status(HttpStatus.CREATED).body(StopListResponse.from(stopItem));
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> remove(@PathVariable UUID venueId, @PathVariable UUID productId) {
        venueStopItemService.remove(venueId, productId);
        return ResponseEntity.noContent().build();
    }
}

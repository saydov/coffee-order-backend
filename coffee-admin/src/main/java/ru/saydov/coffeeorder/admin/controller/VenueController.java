package ru.saydov.coffeeorder.admin.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.saydov.coffeeorder.admin.dto.venue.VenueRequest;
import ru.saydov.coffeeorder.admin.dto.venue.VenueResponse;
import ru.saydov.coffeeorder.shared.service.VenueService;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер управления кофейнями в админке.
 *
 * <p>Возвращает все кофейни независимо от флага {@code active} — админу
 * нужен полный список для управления. Клиентское API видит только
 * активные кофейни.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/venues")
public class VenueController {

    private final VenueService venueService;

    @GetMapping
    public List<VenueResponse> findAll() {
        return venueService.findAll().stream()
                .map(VenueResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public VenueResponse findById(@PathVariable UUID id) {
        return VenueResponse.from(venueService.findById(id));
    }

    @PostMapping
    public ResponseEntity<VenueResponse> create(@Valid @RequestBody VenueRequest request) {
        var venue = venueService.create(
                request.getName(),
                request.getAddress(),
                request.getLatitude(),
                request.getLongitude());
        return ResponseEntity.status(HttpStatus.CREATED).body(VenueResponse.from(venue));
    }

    @PutMapping("/{id}")
    public VenueResponse update(@PathVariable UUID id, @Valid @RequestBody VenueRequest request) {
        var venue = venueService.update(
                id,
                request.getName(),
                request.getAddress(),
                request.isActive(),
                request.getLatitude(),
                request.getLongitude());
        return VenueResponse.from(venue);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        venueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

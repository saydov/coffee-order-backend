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
import ru.saydov.coffeeorder.admin.dto.addon.AddonRequest;
import ru.saydov.coffeeorder.admin.dto.addon.AddonResponse;
import ru.saydov.coffeeorder.shared.service.ProductAddonService;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер управления добавками к продуктам.
 *
 * <p>Вложен в URL продукта ({@code /api/products/{productId}/addons})
 * чтобы подчеркнуть принадлежность: добавка существует только в контексте
 * продукта и удаляется вместе с ним.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products/{productId}/addons")
public class ProductAddonController {

    private final ProductAddonService productAddonService;

    @GetMapping
    public List<AddonResponse> findAll(@PathVariable UUID productId) {
        return productAddonService.findAllByProduct(productId).stream()
                .map(AddonResponse::from)
                .toList();
    }

    @PostMapping
    public ResponseEntity<AddonResponse> create(@PathVariable UUID productId,
                                                @Valid @RequestBody AddonRequest request) {
        var addon = productAddonService.create(productId, request.getName(), request.getPrice());
        return ResponseEntity.status(HttpStatus.CREATED).body(AddonResponse.from(addon));
    }

    @DeleteMapping("/{addonId}")
    public ResponseEntity<Void> delete(@PathVariable UUID addonId) {
        productAddonService.delete(addonId);
        return ResponseEntity.noContent().build();
    }
}

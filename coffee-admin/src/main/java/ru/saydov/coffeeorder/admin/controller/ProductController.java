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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.saydov.coffeeorder.admin.dto.product.ProductRequest;
import ru.saydov.coffeeorder.admin.dto.product.ProductResponse;
import ru.saydov.coffeeorder.admin.dto.product.ProductUpdateRequest;
import ru.saydov.coffeeorder.shared.service.ProductService;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер управления продуктами в админке.
 *
 * <p>Эндпоинты с фильтром по категории отдают тот же ответ что и без —
 * плоский список {@link ProductResponse}. Пагинация пока не реализована,
 * так как каталог обычно умещается в тысячи позиций.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponse> findAll(@RequestParam(required = false) UUID categoryId) {
        var products = categoryId != null
                ? productService.findAllByCategory(categoryId)
                : productService.findAll();
        return products.stream()
                .map(ProductResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable UUID id) {
        return ProductResponse.from(productService.findById(id));
    }

    @PostMapping
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        var product = productService.create(
                request.getCategoryId(),
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getImageUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(ProductResponse.from(product));
    }

    @PutMapping("/{id}")
    public ProductResponse update(@PathVariable UUID id, @Valid @RequestBody ProductUpdateRequest request) {
        var product = productService.update(
                id,
                request.getCategoryId(),
                request.getName(),
                request.getDescription(),
                request.getPrice(),
                request.getImageUrl());
        return ProductResponse.from(product);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

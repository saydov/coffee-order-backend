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
import ru.saydov.coffeeorder.admin.dto.category.CategoryRequest;
import ru.saydov.coffeeorder.admin.dto.category.CategoryResponse;
import ru.saydov.coffeeorder.admin.dto.category.CategoryUpdateRequest;
import ru.saydov.coffeeorder.shared.service.CategoryService;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер управления категориями в админке.
 *
 * <p>Удаление категории не каскадирует — если к категории привязаны
 * продукты, БД вернёт ошибку целостности и сервис её не поймает.
 * Это ожидаемое поведение: админ должен сначала переместить продукты.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> findAll() {
        return categoryService.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public CategoryResponse findById(@PathVariable UUID id) {
        return CategoryResponse.from(categoryService.findById(id));
    }

    @PostMapping
    public ResponseEntity<CategoryResponse> create(@Valid @RequestBody CategoryRequest request) {
        var category = categoryService.create(request.getName(), request.getIconUrl());
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoryResponse.from(category));
    }

    @PutMapping("/{id}")
    public CategoryResponse update(@PathVariable UUID id, @Valid @RequestBody CategoryUpdateRequest request) {
        return CategoryResponse.from(categoryService.update(id, request.getName()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

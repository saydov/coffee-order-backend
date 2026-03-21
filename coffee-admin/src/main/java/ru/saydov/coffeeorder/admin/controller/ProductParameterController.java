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
import ru.saydov.coffeeorder.admin.dto.parameter.ParameterRequest;
import ru.saydov.coffeeorder.admin.dto.parameter.ParameterResponse;
import ru.saydov.coffeeorder.shared.service.ProductParameterService;

import java.util.List;
import java.util.UUID;

/**
 * REST-контроллер управления параметрами продуктов.
 *
 * <p>Обновление параметра не предусмотрено — поменять набор допустимых
 * значений безопасно только через удаление и пересоздание. Это защищает
 * историю заказов от потери связи со значением, выбранным клиентом.
 */
@RequiredArgsConstructor
@RestController
@RequestMapping("/api/products/{productId}/parameters")
public class ProductParameterController {

    private final ProductParameterService productParameterService;

    @GetMapping
    public List<ParameterResponse> findAll(@PathVariable UUID productId) {
        return productParameterService.findAllByProduct(productId).stream()
                .map(ParameterResponse::from)
                .toList();
    }

    @PostMapping
    public ResponseEntity<ParameterResponse> create(@PathVariable UUID productId,
                                                    @Valid @RequestBody ParameterRequest request) {
        var parameter = productParameterService.create(
                productId,
                request.getName(),
                request.toParameterValues());
        return ResponseEntity.status(HttpStatus.CREATED).body(ParameterResponse.from(parameter));
    }

    @DeleteMapping("/{parameterId}")
    public ResponseEntity<Void> delete(@PathVariable UUID parameterId) {
        productParameterService.delete(parameterId);
        return ResponseEntity.noContent().build();
    }
}

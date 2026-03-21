package ru.saydov.coffeeorder.shared.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.saydov.coffeeorder.shared.entity.ParameterValue;
import ru.saydov.coffeeorder.shared.entity.ProductParameter;
import ru.saydov.coffeeorder.shared.exception.ProductParameterNotFoundException;
import ru.saydov.coffeeorder.shared.repository.ProductParameterRepository;

import java.util.List;
import java.util.UUID;

/**
 * Сервис управления параметрами продуктов.
 *
 * <p>Параметр — характеристика с фиксированным набором значений
 * ({@link ParameterValue}), каждое из которых может нести ценовой
 * модификатор. Пример: параметр «Размер» со значениями
 * «200 мл» (0 ₽) / «400 мл» (+100 ₽).
 */
@RequiredArgsConstructor
@Service
public class ProductParameterService {

    private final ProductParameterRepository productParameterRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public List<ProductParameter> findAllByProduct(UUID productId) {
        return productParameterRepository.findAllByProductId(productId);
    }

    @Transactional(readOnly = true)
    public ProductParameter findById(UUID id) {
        return productParameterRepository.findById(id).orElseThrow(() -> new ProductParameterNotFoundException(id));
    }

    @Transactional
    public ProductParameter create(UUID productId, String name, List<ParameterValue> allowedValues) {
        var product = productService.findById(productId);
        var parameter = ProductParameter.builder()
                .product(product)
                .name(name)
                .allowedValues(allowedValues)
                .build();
        return productParameterRepository.save(parameter);
    }

    @Transactional
    public void delete(UUID id) {
        if (!productParameterRepository.existsById(id)) {
            throw new ProductParameterNotFoundException(id);
        }
        productParameterRepository.deleteById(id);
    }
}

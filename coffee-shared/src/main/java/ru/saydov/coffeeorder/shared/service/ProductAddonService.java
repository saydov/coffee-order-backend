package ru.saydov.coffeeorder.shared.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.saydov.coffeeorder.shared.entity.ProductAddon;
import ru.saydov.coffeeorder.shared.exception.ProductAddonNotFoundException;
import ru.saydov.coffeeorder.shared.repository.ProductAddonRepository;

import java.util.List;
import java.util.UUID;

/**
 * Сервис управления добавками к продуктам.
 *
 * <p>Добавки — плоский набор опций: никаких значений и модификаторов,
 * только название и цена. Сложные опции с выбором значения
 * обрабатываются через {@link ProductParameterService}.
 */
@RequiredArgsConstructor
@Service
public class ProductAddonService {

    private final ProductAddonRepository productAddonRepository;
    private final ProductService productService;

    @Transactional(readOnly = true)
    public List<ProductAddon> findAllByProduct(UUID productId) {
        return productAddonRepository.findAllByProductId(productId);
    }

    @Transactional(readOnly = true)
    public ProductAddon findById(UUID id) {
        return productAddonRepository.findById(id).orElseThrow(() -> new ProductAddonNotFoundException(id));
    }

    @Transactional
    public ProductAddon create(UUID productId, String name, Integer price) {
        var product = productService.findById(productId);
        var addon = ProductAddon.builder().product(product).name(name).price(price).build();
        return productAddonRepository.save(addon);
    }

    @Transactional
    public void delete(UUID id) {
        if (!productAddonRepository.existsById(id)) {
            throw new ProductAddonNotFoundException(id);
        }
        productAddonRepository.deleteById(id);
    }
}

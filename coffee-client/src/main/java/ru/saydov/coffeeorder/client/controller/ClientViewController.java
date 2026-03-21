package ru.saydov.coffeeorder.client.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.saydov.coffeeorder.client.config.VenueScope;
import ru.saydov.coffeeorder.client.dto.category.ClientCategoryResponse;
import ru.saydov.coffeeorder.client.dto.product.ClientProductResponse;
import ru.saydov.coffeeorder.client.dto.venue.ClientVenueResponse;
import ru.saydov.coffeeorder.shared.service.CategoryService;
import ru.saydov.coffeeorder.shared.service.ProductService;
import ru.saydov.coffeeorder.shared.service.VenueService;

/**
 * Контроллер отдачи Thymeleaf-страницы клиентского приложения.
 *
 * <p>Отдаёт единственную страницу SPA с предзагруженными данными каталога,
 * чтобы избежать ненужных AJAX-запросов на первый экран. В режиме
 * venue-scoped (инстанс привязан к конкретной кофейне) список кофеен
 * не передаётся на фронт — выбирать не из чего.
 */
@RequiredArgsConstructor
@Controller
public class ClientViewController {

    private final VenueScope venueScope;
    private final VenueService venueService;
    private final ProductService productService;
    private final CategoryService categoryService;

    @Value("${app.map-api-key:}")
    private String mapApiKey;

    @Transactional(readOnly = true)
    @GetMapping("/")
    public String index(Model model) {
        var configured = venueScope.configured();
        model.addAttribute("fixedVenueId", configured.orElse(null));

        if (configured.isEmpty()) {
            var venues = venueService.findAllActive().stream()
                    .map(ClientVenueResponse::from)
                    .toList();
            model.addAttribute("venues", venues);
        }

        var products = productService.findAll().stream()
                .map(ClientProductResponse::from)
                .toList();
        model.addAttribute("products", products);

        var categories = categoryService.findAll().stream()
                .map(ClientCategoryResponse::from)
                .toList();
        model.addAttribute("categories", categories);
        model.addAttribute("mapApiKey", mapApiKey);

        return "client/index";
    }
}

package ru.saydov.coffeeorder.admin.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.saydov.coffeeorder.admin.dto.category.CategoryResponse;
import ru.saydov.coffeeorder.admin.dto.order.OrderResponse;
import ru.saydov.coffeeorder.admin.dto.product.ProductResponse;
import ru.saydov.coffeeorder.admin.dto.venue.VenueResponse;
import ru.saydov.coffeeorder.shared.service.CategoryService;
import ru.saydov.coffeeorder.shared.service.OrderService;
import ru.saydov.coffeeorder.shared.service.ProductService;
import ru.saydov.coffeeorder.shared.service.VenueService;
import ru.saydov.coffeeorder.shared.service.VenueStopItemService;

import java.util.UUID;

/**
 * Thymeleaf-контроллер страниц админ-дашборда.
 *
 * <p>Ответы — имена шаблонов, данные передаются в модель.
 * Навигация стартует с {@code /admin/venues} — это фиксировано
 * в {@link #index()} и в {@code SecurityConfig.defaultSuccessUrl}.
 */
@RequiredArgsConstructor
@Controller
@RequestMapping("/admin")
public class DashboardController {

    private final VenueService venueService;
    private final ProductService productService;
    private final OrderService orderService;
    private final CategoryService categoryService;
    private final VenueStopItemService venueStopItemService;

    @GetMapping
    public String index() {
        return "redirect:/admin/venues";
    }

    @GetMapping("/venues")
    public String venues(Model model) {
        var venues = venueService.findAll().stream()
                .map(VenueResponse::from)
                .toList();
        model.addAttribute("venues", venues);
        return "admin/venues";
    }

    @GetMapping("/products")
    public String products(Model model) {
        var products = productService.findAll().stream()
                .map(ProductResponse::from)
                .toList();
        var categories = categoryService.findAll().stream()
                .map(CategoryResponse::from)
                .toList();

        model.addAttribute("products", products);
        model.addAttribute("categories", categories);
        return "admin/products";
    }

    @GetMapping("/categories")
    public String categories(Model model) {
        var categories = categoryService.findAll().stream()
                .map(CategoryResponse::from)
                .toList();
        model.addAttribute("categories", categories);
        return "admin/categories";
    }

    @GetMapping("/venues/{id}/stop-list")
    public String stopList(@PathVariable UUID id, Model model) {
        var venue = VenueResponse.from(venueService.findById(id));
        var stoppedProductIds = venueStopItemService.findStoppedProductIds(id);
        var allProducts = productService.findAll().stream()
                .map(ProductResponse::from)
                .toList();

        model.addAttribute("venue", venue);
        model.addAttribute("stoppedProductIds", stoppedProductIds);
        model.addAttribute("products", allProducts);
        return "admin/stop-list";
    }

    @GetMapping("/orders")
    public String orders(@RequestParam(required = false) UUID venueId, Model model) {
        var venues = venueService.findAll().stream()
                .map(VenueResponse::from)
                .toList();
        model.addAttribute("venues", venues);

        if (venueId != null) {
            var orders = orderService.findAllByVenue(venueId).stream()
                    .map(OrderResponse::from)
                    .toList();
            model.addAttribute("selectedVenueId", venueId);
            model.addAttribute("orders", orders);
        }

        return "admin/orders";
    }
}

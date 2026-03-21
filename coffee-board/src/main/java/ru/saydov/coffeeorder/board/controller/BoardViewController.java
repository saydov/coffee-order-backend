package ru.saydov.coffeeorder.board.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import ru.saydov.coffeeorder.board.config.VenueScope;
import ru.saydov.coffeeorder.board.dto.venue.BoardVenueResponse;
import ru.saydov.coffeeorder.shared.service.VenueService;

/**
 * Thymeleaf-контроллер главной страницы табло.
 *
 * <p>В venue-scoped режиме список кофеен не передаётся в модель —
 * табло сразу покажет заказы нужной точки. В multi-venue режиме
 * отдаётся список активных кофеен для выбора.
 */
@RequiredArgsConstructor
@Controller
public class BoardViewController {

    private final VenueScope venueScope;
    private final VenueService venueService;

    @GetMapping("/")
    public String index(Model model) {
        var configured = venueScope.configured();
        model.addAttribute("fixedVenueId", configured.orElse(null));
        if (configured.isEmpty()) {
            var venues = venueService.findAllActive().stream()
                    .map(BoardVenueResponse::from)
                    .toList();
            model.addAttribute("venues", venues);
        }
        return "board/index";
    }
}

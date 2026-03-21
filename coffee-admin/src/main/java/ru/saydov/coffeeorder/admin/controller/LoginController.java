package ru.saydov.coffeeorder.admin.controller;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Контроллер страницы логина админки.
 *
 * <p>Отдаёт форму логина анонимным пользователям, уже аутентифицированных
 * редиректит на дашборд. Сама обработка {@code POST /login} выполняется
 * формой Spring Security, настроенной в {@code SecurityConfig}.
 */
@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated()) {
            return "redirect:/admin/venues";
        }
        return "login";
    }
}

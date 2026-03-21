package ru.saydov.coffeeorder.admin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Точка входа админского приложения.
 *
 * <p>Админка обслуживает одновременно REST API под {@code /api/**}
 * и Thymeleaf-интерфейс под {@code /admin/**}. Авторизация
 * одинаковая для обеих веток — form login и HTTP Basic, см.
 * {@code SecurityConfig}.
 */
@SpringBootApplication(scanBasePackages = "ru.saydov.coffeeorder")
@EntityScan("ru.saydov.coffeeorder.shared.entity")
@EnableJpaRepositories("ru.saydov.coffeeorder.shared.repository")
public class CoffeeAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoffeeAdminApplication.class, args);
    }
}

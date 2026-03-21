package ru.saydov.coffeeorder.board;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Точка входа приложения-табло.
 *
 * <p>Показывает активные заказы конкретной кофейни в секциях
 * «Новые», «Готовятся», «Готовы». Запускается обычно на планшете
 * в зале с attach к venueId через переменную окружения.
 */
@SpringBootApplication(scanBasePackages = "ru.saydov.coffeeorder")
@EntityScan("ru.saydov.coffeeorder.shared.entity")
@EnableJpaRepositories("ru.saydov.coffeeorder.shared.repository")
public class CoffeeBoardApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoffeeBoardApplication.class, args);
    }
}

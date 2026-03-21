package ru.saydov.coffeeorder.client;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Точка входа клиентского приложения.
 *
 * <p>Обслуживает REST API для мобильного/веб-клиента и рендерит Thymeleaf-представления
 * для результата оплаты. {@code @EnableScheduling} активирует фоновый heartbeat
 * для SSE-соединений в {@code OrderSseService}.
 */
@SpringBootApplication(scanBasePackages = "ru.saydov.coffeeorder")
@EntityScan("ru.saydov.coffeeorder.shared.entity")
@EnableJpaRepositories("ru.saydov.coffeeorder.shared.repository")
@EnableScheduling
public class CoffeeClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoffeeClientApplication.class, args);
    }
}

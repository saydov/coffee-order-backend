package ru.saydov.coffeeorder.client.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки подключения к платёжному шлюзу PayBox.
 *
 * <p>Читаются из конфигурации по префиксу {@code payment}. Immutable —
 * поля {@code private final}, в противовес типичному {@code @Setter}-паттерну
 * Spring Boot: значения фиксируются в момент создания бина и не могут
 * быть изменены после старта.
 */
@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "payment")
public class PaymentProperties {

    private final String baseUrl;

    private final String apiKey;
}

package ru.saydov.coffeeorder.client.config;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки подписи и срока жизни JWT-токенов.
 *
 * <p>Значения обязательно задаются в конфигурации ({@code application.yml} или
 * переменные окружения с префиксом {@code jwt}) — дефолтные значения
 * в коде не задаются, чтобы случайный прод-деплой без настроек
 * не использовал небезопасный hardcoded-секрет.
 */
@Getter
@RequiredArgsConstructor
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    private final String secret;

    private final long expirationMs;
}

package ru.saydov.coffeeorder.client.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

/**
 * Конфигурация HTTP-клиента для платёжного шлюза.
 *
 * <p>Создаёт {@link RestClient} с предустановленным базовым URL
 * и API-ключом — остальная логика взаимодействия с PayBox живёт
 * в {@code PaymentClient}. Разделение на config и client
 * упрощает подмену транспорта в тестах через тестовый {@link RestClient}.
 */
@Configuration
@EnableConfigurationProperties({PaymentProperties.class, JwtProperties.class})
public class PaymentClientConfig {

    @Bean
    public RestClient paymentRestClient(PaymentProperties properties) {
        return RestClient.builder()
                .baseUrl(properties.getBaseUrl())
                .defaultHeader("X-API-Key", properties.getApiKey())
                .build();
    }
}

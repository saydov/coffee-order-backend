package ru.saydov.coffeeorder.client.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import ru.saydov.coffeeorder.client.security.JwtAuthFilter;

/**
 * Spring Security для клиентского API.
 *
 * <p>Stateless-авторизация через JWT (фильтр {@link JwtAuthFilter}
 * поставлен до {@code UsernamePasswordAuthenticationFilter}).
 * Каталог и auth-эндпоинты доступны анонимно; операции с профилем
 * и заказами требуют JWT.
 *
 * <p>SSE-эндпоинт вынесен в publicly accessible — токен в нём передаётся
 * query-параметром, так как браузерный {@code EventSource} не умеет
 * ставить кастомные заголовки; проверка выполняется в
 * {@code OrderSseController}.
 */
@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/api/client/auth/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/client/orders/*/stream").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/client/categories").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/client/products").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/client/venues/**").permitAll()
                        .requestMatchers("/payment/**").permitAll()
                        .requestMatchers("/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/api/client/**").authenticated()
                        .anyRequest().permitAll())
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}

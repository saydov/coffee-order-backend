package ru.saydov.coffeeorder.admin.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Arrays;
import java.util.List;

/**
 * Spring Security для админского приложения.
 *
 * <p>Пользователи с ролью {@code ADMIN} загружаются из файла
 * {@code classpath:users.json} в память — никаких запросов к БД
 * по авторизации. Такое решение подходит небольшой команде; при росте
 * числа админов логичнее перейти на таблицу в БД или внешнего провайдера.
 *
 * <p>CSRF отключён, так как интерфейс работает в одном origin
 * и основные операции выполняются через API-запросы от авторизованного
 * session-based клиента.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .authorizeHttpRequests(auth -> auth.anyRequest().hasRole("ADMIN"))
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/admin/venues", true)
                        .permitAll())
                .httpBasic(Customizer.withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .build();
    }

    @Bean
    public UserDetailsService userDetailsService(ObjectMapper objectMapper, ResourceLoader resourceLoader) throws Exception {
        var resource = resourceLoader.getResource("classpath:users.json");
        var entries = objectMapper.readValue(resource.getInputStream(), UserEntry[].class);
        var users = Arrays.stream(entries).map(UserEntry::toDetails).toList();
        return new InMemoryUserDetailsManager(users);
    }

    /**
     * Запись о пользователе-админе, загруженная из {@code users.json}.
     *
     * <p>Пароль хранится в формате, принимаемом
     * {@link org.springframework.security.core.userdetails.User.UserBuilder#password} —
     * обычно с префиксом {@code {bcrypt}} или {@code {noop}} для dev-окружения.
     */
    @Getter
    @Builder
    @ToString
    @Jacksonized
    static class UserEntry {

        private final String username;

        private final String password;

        private final List<String> roles;

        UserDetails toDetails() {
            return User.withUsername(username)
                    .password(password)
                    .roles(roles.toArray(String[]::new))
                    .build();
        }
    }
}

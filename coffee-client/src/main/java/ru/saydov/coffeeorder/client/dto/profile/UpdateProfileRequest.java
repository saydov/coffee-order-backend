package ru.saydov.coffeeorder.client.dto.profile;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * Запрос на обновление имени клиента.
 *
 * <p>Сейчас разрешает менять только имя — остальные поля профиля
 * иммутабельны (телефон фиксируется верификацией, бонусы считаются сервисом).
 */
@Getter
@Builder
@ToString
@Jacksonized
public class UpdateProfileRequest {

    @NotBlank
    private final String name;
}

package ru.saydov.coffeeorder.client.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * Запрос на проверку SMS-кода и выдачу JWT.
 *
 * <p>Номер телефона должен совпадать с тем, на который запрашивался код;
 * иначе проверка будет искать несуществующую запись в Redis и вернёт 401.
 */
@Getter
@Builder
@ToString
@Jacksonized
public class VerifyCodeRequest {

    @NotBlank
    @Pattern(regexp = "^\\+7\\d{10}$")
    private final String phone;

    @NotBlank
    @Pattern(regexp = "^\\d{4}$")
    private final String code;
}

package ru.saydov.coffeeorder.client.dto.auth;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

/**
 * Запрос на отправку SMS-кода верификации.
 *
 * <p>Принимает российский номер в формате E.164 ({@code +7XXXXXXXXXX}).
 * Расширение на международные форматы потребует изменения регулярного
 * выражения — сейчас оно сознательно жёсткое, чтобы отсечь опечатки.
 */
@Getter
@Builder
@ToString
@Jacksonized
public class SendCodeRequest {

    @NotBlank
    @Pattern(regexp = "^\\+7\\d{10}$", message = "Формат: +7XXXXXXXXXX")
    private final String phone;
}

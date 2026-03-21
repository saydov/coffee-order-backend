package ru.saydov.coffeeorder.client.dto.auth;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Ответ на успешную SMS-верификацию.
 *
 * <p>Токен передаётся в заголовке {@code Authorization: Bearer <token>}
 * при последующих запросах. Флаг {@code newUser} помогает фронту
 * различать регистрацию (показать онбординг, собрать имя)
 * от обычного входа.
 */
@Getter
@Builder
@ToString
public class AuthResponse {

    private final String token;

    private final boolean newUser;
}

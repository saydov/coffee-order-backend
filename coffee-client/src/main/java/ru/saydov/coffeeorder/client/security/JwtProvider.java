package ru.saydov.coffeeorder.client.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.saydov.coffeeorder.client.config.JwtProperties;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * Провайдер JWT-токенов — подпись и верификация.
 *
 * <p>Использует HS256: симметричная подпись секретом из {@link JwtProperties}.
 * В токене хранится только {@code subject} — UUID клиента, что достаточно
 * для stateless-авторизации без обращения к БД.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class JwtProvider {

    private final JwtProperties jwtProperties;

    /**
     * Создаёт подписанный JWT-токен для клиента.
     *
     * <p>Срок жизни — {@code jwt.expiration-ms} из конфигурации, обычно неделя.
     * После истечения клиент обязан повторить SMS-верификацию.
     *
     * @param clientId идентификатор клиента, попадающий в поле {@code sub}
     * @return compact-представление токена для передачи в HTTP-заголовке
     */
    public String generateToken(UUID clientId) {
        var now = new Date();
        var expiry = new Date(now.getTime() + jwtProperties.getExpirationMs());
        
        return Jwts.builder()
                .subject(clientId.toString())
                .issuedAt(now)
                .expiration(expiry)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * Извлекает идентификатор клиента из валидного токена.
     *
     * <p>Предполагает, что токен уже прошёл проверку через {@link #isValid} —
     * при невалидном токене метод бросит исключение парсинга JWT.
     *
     * @param token JWT-токен
     * @return идентификатор клиента из поля {@code sub}
     */
    public UUID getClientId(String token) {
        return UUID.fromString(parseClaims(token).getSubject());
    }

    /**
     * Проверяет валидность токена: подпись, срок действия, формат.
     *
     * <p>Невалидный токен — ожидаемое состояние (просроченный, подделанный,
     * обрезанный) — поэтому исключение логируется на уровне debug и
     * возвращается {@code false}, а не пробрасывается наружу.
     *
     * @param token проверяемый токен
     * @return {@code true} если токен корректно подписан и не просрочен
     */
    public boolean isValid(String token) {
        try {
            parseClaims(token);
            return true;
        } catch (Exception e) {
            log.debug("Invalid JWT rejected: {}", e.getMessage());
            return false;
        }
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }
}

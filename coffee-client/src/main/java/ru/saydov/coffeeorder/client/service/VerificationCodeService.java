package ru.saydov.coffeeorder.client.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Сервис SMS-кодов верификации номера телефона.
 *
 * <p>Код — 4 случайные цифры, хранятся в Redis с TTL
 * {@value #CODE_TTL_MINUTES} минут. На проверке код сразу удаляется
 * (одноразовый) — повторная верификация требует нового кода.
 *
 * <p><b>TODO:</b> интеграция с реальным SMS-провайдером;
 * сейчас код только логируется, что допустимо только в dev-окружении.
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class VerificationCodeService {

    private static final String KEY_PREFIX = "verify:";
    private static final int CODE_TTL_MINUTES = 5;
    private static final Duration CODE_TTL = Duration.ofMinutes(CODE_TTL_MINUTES);
    private static final int CODE_RANGE = 10_000;
    private static final String CODE_FORMAT = "%04d";

    private final StringRedisTemplate redisTemplate;

    public String generateAndStore(String phone) {
        var code = String.format(CODE_FORMAT, ThreadLocalRandom.current().nextInt(CODE_RANGE));
        redisTemplate.opsForValue().set(key(phone), code, CODE_TTL);
        // TODO: отправка SMS через провайдера
        log.info("Verification code for {}: {}", phone, code);
        return code;
    }

    public boolean verify(String phone, String code) {
        var stored = redisTemplate.opsForValue().get(key(phone));
        if (stored != null && stored.equals(code)) {
            redisTemplate.delete(key(phone));
            return true;
        }
        return false;
    }

    private String key(String phone) {
        return KEY_PREFIX + phone;
    }
}

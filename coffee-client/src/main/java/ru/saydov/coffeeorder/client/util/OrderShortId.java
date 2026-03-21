package ru.saydov.coffeeorder.client.util;

import lombok.experimental.UtilityClass;

import java.util.UUID;

/**
 * Короткая человекочитаемая версия UUID заказа.
 *
 * <p>Используется для отображения клиенту (чек, страница успеха,
 * описание платежа) — полный UUID слишком длинный, {@value #LENGTH}
 * символа достаточно, чтобы идентифицировать заказ в разговоре
 * с бариста. Не используется как уникальный идентификатор в БД —
 * коллизии возможны, но в пределах одной точки за день маловероятны.
 */
@UtilityClass
public class OrderShortId {

    private static final int LENGTH = 6;

    /**
     * Возвращает короткий id заказа в upper-case.
     *
     * <p>Убирает дефисы из UUID и берёт первые {@value #LENGTH} символа —
     * так получается компактный код вида {@code "A3F2B1"}.
     *
     * @param id UUID заказа
     * @return короткий идентификатор из {@value #LENGTH} символов
     */
    public String of(UUID id) {
        return id.toString().replace("-", "").substring(0, LENGTH).toUpperCase();
    }
}

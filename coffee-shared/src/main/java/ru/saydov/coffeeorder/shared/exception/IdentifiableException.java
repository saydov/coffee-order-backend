package ru.saydov.coffeeorder.shared.exception;

import lombok.experimental.StandardException;

import java.util.UUID;

/**
 * Базовое исключение для сущностей, не найденных по идентификатору.
 *
 * <p>Сообщение формируется в формате {@code id=<uuid>}, что соответствует
 * требованию style guide передавать в исключения минимальный идентификатор
 * в виде пары ключ-значение. Конкретные подклассы не переопределяют формат —
 * тип исключения сам несёт информацию о домене.
 */
@StandardException
public abstract class IdentifiableException extends RuntimeException {

    protected IdentifiableException(UUID id) {
        super("id=" + id);
    }

    protected IdentifiableException(UUID id, Throwable cause) {
        super("id=" + id, cause);
    }
}

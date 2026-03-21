package ru.saydov.coffeeorder.admin.advice;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.saydov.coffeeorder.shared.exception.IdentifiableException;

/**
 * Глобальный обработчик исключений для REST-контроллеров админки.
 *
 * <p>Маппит семейство {@link IdentifiableException} (все {@code *NotFoundException}
 * из shared-модуля) в HTTP 404. Отдельные обработчики на каждый тип
 * не нужны — общий предок гарантирует единообразный маппинг и
 * избавляет от необходимости модифицировать advice при добавлении
 * новых типов not-found исключений.
 */
@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(IdentifiableException.class)
    public ResponseEntity<Void> handleNotFound(IdentifiableException e) {
        return ResponseEntity.notFound().build();
    }
}

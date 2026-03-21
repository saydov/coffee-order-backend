package ru.saydov.coffeeorder.client.dto.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;
import lombok.extern.jackson.Jacksonized;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Ответ платёжного шлюза с данными о платеже.
 *
 * <p>Содержит URL для перенаправления пользователя на страницу оплаты
 * и текущий статус платежа. Статус — строка, так как возможные значения
 * определяются провайдером и могут расширяться без координации
 * ({@code CREATED}, {@code PENDING}, {@code PAID}, {@code FAILED}
 * и так далее).
 *
 * @see CreatePaymentRequest
 */
@Getter
@Builder
@ToString
@Jacksonized
public class PaymentResponse {

    private final UUID id;

    private final String orderId;

    private final double amount;

    private final String currency;

    private final String status;

    private final String method;

    private final String paymentUrl;

    private final LocalDateTime createdAt;

    private final LocalDateTime paidAt;
}

package ru.saydov.coffeeorder.client.dto.payment;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

/**
 * Запрос на создание платежа в платёжном шлюзе.
 *
 * <p>Формируется на основе данных заказа и отправляется в PayBox API.
 * Валюта передаётся строкой (ISO-4217), что соответствует API шлюза.
 *
 * @see PaymentResponse
 */
@Getter
@Builder
@ToString
public class CreatePaymentRequest {

    private final double amount;

    private final String currency;

    private final String orderId;

    private final String description;

    private final String redirectUrl;
}

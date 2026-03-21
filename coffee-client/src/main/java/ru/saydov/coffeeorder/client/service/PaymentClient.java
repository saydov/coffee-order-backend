package ru.saydov.coffeeorder.client.service;

import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import ru.saydov.coffeeorder.client.dto.payment.CreatePaymentRequest;
import ru.saydov.coffeeorder.client.dto.payment.PaymentResponse;

import java.util.List;
import java.util.UUID;

/**
 * HTTP-клиент для взаимодействия с платёжным шлюзом PayBox.
 *
 * <p>Обёртка над сконфигурированным {@link RestClient}: базовый URL
 * и API-ключ уже предустановлены в {@code PaymentClientConfig},
 * поэтому методам достаточно указать относительный путь.
 *
 * @see CreatePaymentRequest
 * @see PaymentResponse
 */
@RequiredArgsConstructor
@Service
public class PaymentClient {

    private final RestClient paymentRestClient;

    /**
     * Создаёт платёж в шлюзе.
     *
     * <p>Отправляет запрос с суммой, описанием и URL для редиректа
     * после оплаты. Возвращает ответ с {@code paymentUrl} для перенаправления
     * пользователя на страницу оплаты.
     *
     * @param request данные для создания платежа
     * @return ответ шлюза с идентификатором и ссылкой на оплату
     */
    public PaymentResponse create(CreatePaymentRequest request) {
        return paymentRestClient.post()
                .uri("/api/v1/payments")
                .contentType(MediaType.APPLICATION_JSON)
                .body(request)
                .retrieve()
                .body(PaymentResponse.class);
    }

    /**
     * Получает текущее состояние платежа по идентификатору.
     *
     * @param paymentId идентификатор платежа
     * @return актуальные данные платежа
     */
    public PaymentResponse getPayment(UUID paymentId) {
        return paymentRestClient.get()
                .uri("/api/v1/payments/{id}", paymentId)
                .retrieve()
                .body(PaymentResponse.class);
    }

    /**
     * Запрашивает у провайдера актуальный статус платежа.
     *
     * <p>Используется после редиректа от платёжного шлюза, чтобы проверить
     * переход статуса из {@code CREATED/PENDING} в терминальное состояние,
     * если webhook от провайдера ещё не дошёл.
     *
     * @param paymentId идентификатор платежа
     * @return обновлённые данные платежа
     */
    public PaymentResponse refreshStatus(UUID paymentId) {
        return paymentRestClient.post()
                .uri("/api/v1/payments/{id}/refresh", paymentId)
                .retrieve()
                .body(PaymentResponse.class);
    }

    /**
     * Получает все платежи, привязанные к заказу.
     *
     * <p>Один заказ может иметь несколько платежей (повторные попытки
     * после отказов). Вызывающий код должен сам выбрать релевантный.
     *
     * @param orderId идентификатор заказа
     * @return список платежей по заказу; пуст, если платежей не было
     */
    public List<PaymentResponse> getByOrderId(String orderId) {
        return paymentRestClient.get()
                .uri("/api/v1/payments/by-order/{orderId}", orderId)
                .retrieve()
                .body(new ParameterizedTypeReference<>() {
                });
    }
}

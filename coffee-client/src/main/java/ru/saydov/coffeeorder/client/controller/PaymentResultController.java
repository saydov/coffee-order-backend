package ru.saydov.coffeeorder.client.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.saydov.coffeeorder.client.dto.payment.PaymentResponse;
import ru.saydov.coffeeorder.client.service.PaymentClient;
import ru.saydov.coffeeorder.client.util.OrderShortId;
import ru.saydov.coffeeorder.shared.entity.order.Order;
import ru.saydov.coffeeorder.shared.service.OrderService;

import java.util.List;
import java.util.UUID;

/**
 * Контроллер страниц результата оплаты.
 *
 * <p>Обрабатывает redirectUrl от платёжного шлюза после попытки оплаты:
 * опрашивает актуальный статус платежа и перенаправляет на страницу
 * успеха или ошибки. Если shлюз уже прислал webhook {@code PAID} —
 * идемпотентное подтверждение через {@link OrderService#confirmPayment}
 * не навредит.
 */
@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/payment")
public class PaymentResultController {

    private static final String STATUS_PAID = "PAID";
    private static final String STATUS_CREATED = "CREATED";
    private static final String STATUS_PENDING = "PENDING";
    private static final String STATUS_UNKNOWN = "UNKNOWN";
    private static final String VIEW_SUCCESS = "client/payment-success";
    private static final String VIEW_ERROR = "client/payment-error";

    private final PaymentClient paymentClient;
    private final OrderService orderService;

    /**
     * Обрабатывает результат оплаты после редиректа от платёжного шлюза.
     *
     * <p>Последовательно проверяет: есть ли среди платежей заказа
     * успешный, и если нет — пытается обновить статус у провайдера
     * (webhook мог задержаться). Только при подтверждённой оплате
     * заказ помечается оплаченным — бонусы списываются в сервисе.
     *
     * @param orderId идентификатор заказа
     * @param model   модель для Thymeleaf-шаблона
     * @return имя шаблона — {@value #VIEW_SUCCESS} или {@value #VIEW_ERROR}
     */
    @GetMapping("/result")
    public String paymentResult(@RequestParam UUID orderId, Model model) {
        var order = orderService.findById(orderId);
        var payments = paymentClient.getByOrderId(orderId.toString());

        if (anyPaid(payments) || refreshAndCheckPaid(payments)) {
            orderService.confirmPayment(orderId);
            return renderSuccess(order, model);
        }
        return renderError(orderId, payments, model);
    }

    private boolean anyPaid(List<PaymentResponse> payments) {
        return payments.stream().anyMatch(p -> STATUS_PAID.equals(p.getStatus()));
    }

    private boolean refreshAndCheckPaid(List<PaymentResponse> payments) {
        for (var payment : payments) {
            if (!isInFlight(payment)) {
                continue;
            }
            var refreshed = paymentClient.refreshStatus(payment.getId());
            if (STATUS_PAID.equals(refreshed.getStatus())) {
                return true;
            }
        }
        return false;
    }

    private boolean isInFlight(PaymentResponse payment) {
        return STATUS_CREATED.equals(payment.getStatus()) || STATUS_PENDING.equals(payment.getStatus());
    }

    private String renderSuccess(Order order, Model model) {
        model.addAttribute("orderId", order.getId());
        model.addAttribute("orderNumber", OrderShortId.of(order.getId()));
        model.addAttribute("status", order.getStatus().name());
        return VIEW_SUCCESS;
    }

    private String renderError(UUID orderId, List<PaymentResponse> payments, Model model) {
        var lastStatus = payments.isEmpty()
                ? STATUS_UNKNOWN
                : payments.get(payments.size() - 1).getStatus();
        model.addAttribute("orderId", orderId);
        model.addAttribute("status", lastStatus);
        return VIEW_ERROR;
    }
}

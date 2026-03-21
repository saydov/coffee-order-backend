package ru.saydov.coffeeorder.client.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import ru.saydov.coffeeorder.shared.entity.order.OrderStatus;
import ru.saydov.coffeeorder.shared.event.OrderStatusChangedEvent;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Управление SSE-соединениями для трансляции статусов заказов клиентам.
 *
 * <p>Хранит активные {@link SseEmitter} в памяти, сгруппированные по
 * {@code orderId}. Подписчики получают события о смене статуса
 * через {@link EventListener} от {@link OrderStatusChangedEvent}.
 * Для защиты от разрыва idle-соединений прокси каждые {@value #HEARTBEAT_MS} мс
 * рассылается heartbeat-комментарий.
 *
 * <p><b>Потокобезопасность:</b> {@link ConcurrentHashMap} внешней структуры
 * плюс {@link CopyOnWriteArrayList} для списков emitters — это дорого при
 * записи, но дёшево при чтении (heartbeat и status-события), что и
 * составляет основной паттерн нагрузки.
 *
 * <p><b>Не держит</b> истории событий: подписчик, отвалившийся на секунду,
 * пропустит изменение статуса. В нашем сценарии это допустимо — клиент всё
 * равно увидит актуальный статус при перезагрузке страницы заказа.
 */
@Slf4j
@Service
public class OrderSseService {

    private static final long TIMEOUT_MS = Duration.ofMinutes(10).toMillis();
    private static final long HEARTBEAT_MS = 30_000L;

    private final Map<UUID, CopyOnWriteArrayList<SseEmitter>> emitters = new ConcurrentHashMap<>();

    /**
     * Регистрирует новое SSE-соединение для указанного заказа.
     *
     * <p>Emitter хранится до одного из трёх событий: таймаут, ошибка,
     * или перевод заказа в {@link OrderStatus#DONE} через {@link #onStatusChanged}.
     * Сразу после подписки отправляется событие {@code connected} — это
     * подтверждает клиенту готовность соединения и помогает отличить
     * рабочий канал от «висит в ожидании».
     *
     * @param orderId идентификатор заказа
     * @return готовый emitter для возврата в SSE-endpoint
     */
    public SseEmitter subscribe(UUID orderId) {
        var emitter = new SseEmitter(TIMEOUT_MS);

        emitter.onCompletion(() -> remove(orderId, emitter));
        emitter.onTimeout(() -> remove(orderId, emitter));
        emitter.onError(e -> remove(orderId, emitter));

        emitters.computeIfAbsent(orderId, id -> new CopyOnWriteArrayList<>()).add(emitter);

        try {
            emitter.send(SseEmitter.event().name("connected").data("ok"));
        } catch (IOException e) {
            log.warn("Failed to send initial event for order {}", orderId, e);
            remove(orderId, emitter);
        }

        return emitter;
    }

    @EventListener
    public void onStatusChanged(OrderStatusChangedEvent event) {
        var list = emitters.get(event.getOrderId());
        if (list == null || list.isEmpty()) {
            return;
        }

        var payload = Map.of(
                "status", event.getStatus().name(),
                "changedAt", event.getChangedAt().toString());

        for (var emitter : list) {
            try {
                emitter.send(SseEmitter.event().name("status").data(payload));
            } catch (IOException e) {
                remove(event.getOrderId(), emitter);
            }
        }

        if (event.getStatus() == OrderStatus.DONE) {
            list.forEach(SseEmitter::complete);
            emitters.remove(event.getOrderId());
        }
    }

    @Scheduled(fixedRate = HEARTBEAT_MS)
    public void heartbeat() {
        emitters.forEach((orderId, list) -> {
            for (var emitter : list) {
                try {
                    emitter.send(SseEmitter.event().comment("keepalive"));
                } catch (IOException e) {
                    remove(orderId, emitter);
                }
            }
        });
    }

    private void remove(UUID orderId, SseEmitter emitter) {
        var list = emitters.get(orderId);
        if (list == null) {
            return;
        }
        list.remove(emitter);
        if (list.isEmpty()) {
            emitters.remove(orderId);
        }
    }
}

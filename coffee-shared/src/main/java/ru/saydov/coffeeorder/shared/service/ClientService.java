package ru.saydov.coffeeorder.shared.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.saydov.coffeeorder.shared.entity.Client;
import ru.saydov.coffeeorder.shared.exception.ClientNotFoundException;
import ru.saydov.coffeeorder.shared.repository.ClientRepository;

import java.util.UUID;

/**
 * Сервис управления клиентами.
 *
 * <p>Клиенты создаются лениво при первой верификации SMS-кода —
 * {@link #findOrCreateByPhone(String)} реализует эту логику идемпотентно.
 * Остальные операции (обновление имени, изменение баланса бонусов,
 * выбор предпочитаемой кофейни) предполагают, что клиент уже существует.
 */
@RequiredArgsConstructor
@Service
public class ClientService {

    private final ClientRepository clientRepository;
    private final VenueService venueService;

    @Transactional(readOnly = true)
    public Client findById(UUID id) {
        return clientRepository.findById(id).orElseThrow(() -> new ClientNotFoundException(id));
    }

    /**
     * Возвращает клиента по номеру телефона, создавая запись при первой попытке.
     *
     * <p>Идемпотентная операция — повторные вызовы с тем же телефоном
     * возвращают уже существующую запись, а не создают дубли. Имя и бонусы
     * при автосоздании не устанавливаются — заполняются позже через
     * {@link #updateName} и {@link #addBonusPoints}.
     *
     * @param phone номер телефона в формате E.164 (например, {@code +71234567890})
     * @return существующий или только что созданный клиент
     */
    @Transactional
    public Client findOrCreateByPhone(String phone) {
        return clientRepository.findByPhone(phone)
                .orElseGet(() -> clientRepository.save(Client.builder().phone(phone).build()));
    }

    @Transactional
    public Client updateName(UUID id, String name) {
        var client = findById(id);
        return clientRepository.save(client.toBuilder().name(name).build());
    }

    /**
     * Изменяет баланс бонусных баллов клиента.
     *
     * <p>Принимает положительное значение для начисления и отрицательное
     * для списания. Валидация достаточности баланса — на уровне вызывающего
     * кода ({@link OrderService}), так как метод допускает временный
     * отрицательный баланс в рамках пересчётов.
     *
     * @param id     идентификатор клиента
     * @param points дельта баллов — положительная для начисления, отрицательная для списания
     * @return клиент с обновлённым балансом
     */
    @Transactional
    public Client addBonusPoints(UUID id, int points) {
        var client = findById(id);
        return clientRepository.save(
                client.toBuilder().bonusPoints(client.getBonusPoints() + points).build());
    }

    /**
     * Устанавливает предпочитаемую клиентом кофейню.
     *
     * <p>Кофейня должна быть активна — иначе {@link VenueService#findActiveById}
     * бросит {@code VenueNotFoundException}. Используется для автоподстановки
     * venueId в каталог и заказы, когда клиент явно не указал локацию.
     *
     * @param clientId идентификатор клиента
     * @param venueId  идентификатор активной кофейни
     * @return клиент с обновлённой ссылкой на кофейню
     */
    @Transactional
    public Client setPreferredVenue(UUID clientId, UUID venueId) {
        var client = findById(clientId);
        var venue = venueService.findActiveById(venueId);
        return clientRepository.save(client.toBuilder().preferredVenue(venue).build());
    }
}

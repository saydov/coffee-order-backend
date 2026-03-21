package ru.saydov.coffeeorder.shared.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.saydov.coffeeorder.shared.entity.Client;

import java.util.Optional;
import java.util.UUID;

/**
 * Репозиторий клиентов.
 *
 * <p>Поиск по телефону используется в процессе SMS-верификации —
 * {@code existsByPhone} проверяет «новый vs вернувшийся пользователь»,
 * а {@code findByPhone} подгружает существующую запись для issue JWT.
 */
public interface ClientRepository extends JpaRepository<Client, UUID> {

    Optional<Client> findByPhone(String phone);

    boolean existsByPhone(String phone);
}

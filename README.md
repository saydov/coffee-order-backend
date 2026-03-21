<div align="center">

# ☕ Coffee Order

Бэкенд платформы заказов для кофейни

![Java](https://img.shields.io/badge/Java-21-orange?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.5-6DB33F?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-16-336791?style=for-the-badge&logo=postgresql)
![Redis](https://img.shields.io/badge/Redis-7-DC382D?style=for-the-badge&logo=redis)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?style=for-the-badge&logo=docker)
![React](https://img.shields.io/badge/React-19.1.0-blue?style=for-the-badge&logo=react)

[Front-end](https://github.com/saydov/coffee-order-frontend) · [Быстрый старт](#быстрый-старт-через-docker) · [Архитектура](#архитектура)

</div>

---

## Состав репозитория

| Модуль          | Порт | Назначение                                                                  |
|-----------------|------|-----------------------------------------------------------------------------|
| `coffee-shared` | —    | Общие сущности JPA, репозитории, утилиты                                    |
| `coffee-admin`  | 8080 | Админ-панель (Thymeleaf) + REST API под `/api/**`, управление меню/заказами |
| `coffee-board`  | 8081 | Экран бариста: очередь готовящихся заказов в реальном времени               |
| `coffee-client` | 8082 | Публичный API для мобильного/веб-клиента, JWT-авторизация, оплата          |

---

## Быстрый старт через Docker

Требуется Docker 24+ и Docker Compose v2.

```bash
git clone https://github.com/saydov/coffee-order.git
cd coffee-order

# Скопируйте шаблон переменных окружения и при необходимости подправьте
cp .env.example .env

# Сборка и запуск всего стека (Postgres + Redis + 3 сервиса)
docker compose up -d --build
```

После старта будут доступны:

- Админка — <http://localhost:8080>
- Экран бариста — <http://localhost:8081>
- Клиентский API — <http://localhost:8082>
- PostgreSQL — `localhost:5432` (db: `coffee_order`, user/pass: `postgres/postgres`)
- Redis — `localhost:6379`

### Полезные команды

```bash
docker compose logs -f coffee-admin     # логи одного сервиса
docker compose ps                        # статус контейнеров
docker compose restart coffee-client     # перезапуск конкретного сервиса
docker compose down                      # остановка (данные сохраняются в volume)
docker compose down -v                   # полная очистка вместе с БД
```

### Пересборка после изменений в коде

```bash
docker compose build coffee-admin
docker compose up -d coffee-admin
```

---

## Конфигурация

Все настройки переопределяются через переменные окружения (см. `.env.example`):

| Переменная           | Значение по умолчанию                     | Описание                                    |
|----------------------|-------------------------------------------|---------------------------------------------|
| `POSTGRES_DB`        | `coffee_order`                            | Имя БД                                       |
| `POSTGRES_USER`      | `postgres`                                | Пользователь БД                              |
| `POSTGRES_PASSWORD`  | `postgres`                                | Пароль БД (**замените в проде**)             |
| `VENUE_ID`           | —                                         | UUID кофейни для board/client               |
| `MAP_API_KEY`        | demo                                      | Ключ Яндекс.Карт                             |
| `PAYMENT_BASE_URL`   | `https://paybox.wisterk.me`               | URL платёжного шлюза                         |
| `PAYMENT_API_KEY`    | demo                                      | API-ключ платёжного шлюза                    |
| `JWT_SECRET`         | demo (≥256 бит)                           | Секрет для подписи JWT (**замените в проде**)|
| `JWT_EXPIRATION_MS`  | `604800000` (7 дней)                      | Время жизни токена                          |

---

## Локальная разработка без Docker

```bash
# 1. Postgres + Redis в Docker, остальное — из IDE
docker compose up -d postgres redis

# 2. Сборка
mvn clean install -DskipTests

# 3. Запуск нужного сервиса
mvn -pl coffee-admin spring-boot:run
```

---

## Архитектура

```
┌──────────────────┐   ┌──────────────────┐   ┌──────────────────┐
│  coffee-admin    │   │  coffee-board    │   │  coffee-client   │
│   :8080          │   │   :8081          │   │   :8082          │
│  Thymeleaf + API │   │  Экран бариста   │   │  REST + JWT      │
└────────┬─────────┘   └────────┬─────────┘   └────────┬─────────┘
         │                      │                      │
         └──────────┬───────────┴──────────┬───────────┘
                    │                      │
            ┌───────┴────────┐    ┌────────┴──────┐
            │   PostgreSQL   │    │     Redis     │
            └────────────────┘    └───────────────┘
```

Общая модель данных и репозитории живут в `coffee-shared` и подключаются всеми тремя сервисами как Maven-зависимость.

---

## Лицензия

См. [LICENSE](LICENSE).

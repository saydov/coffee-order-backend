package ru.saydov.coffeeorder.client.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import ru.saydov.coffeeorder.shared.entity.Category;
import ru.saydov.coffeeorder.shared.entity.ParameterValue;
import ru.saydov.coffeeorder.shared.entity.Product;
import ru.saydov.coffeeorder.shared.service.CategoryService;
import ru.saydov.coffeeorder.shared.service.ProductAddonService;
import ru.saydov.coffeeorder.shared.service.ProductParameterService;
import ru.saydov.coffeeorder.shared.service.ProductService;
import ru.saydov.coffeeorder.shared.service.VenueService;

import java.util.List;

/**
 * Наполнение БД демо-данными при первом запуске приложения.
 *
 * <p>Работает только если каталог категорий пуст — повторные рестарты
 * не затрагивают существующие записи. Нужен только для разработки
 * и демо-стенда; на проде должен отключаться отдельным профилем или
 * заменяться на миграции Flyway/Liquibase.
 */
@Slf4j
@RequiredArgsConstructor
@Component
public class DataInitializer implements ApplicationRunner {

    private final CategoryService categoryService;
    private final ProductService productService;
    private final ProductAddonService addonService;
    private final ProductParameterService parameterService;
    private final VenueService venueService;

    @Override
    public void run(ApplicationArguments args) {
        if (!categoryService.findAll().isEmpty()) {
            return;
        }

        log.info("Инициализация тестовых данных...");

        var coffee = categoryService.create("Кофе", "/images/categories/coffee.jpg");
        var tea = categoryService.create("Чай", "/images/categories/tea.jpg");
        var cold = categoryService.create("Холодные напитки", "/images/categories/cold.jpg");
        var pastries = categoryService.create("Пирожные", "/images/categories/pastries.jpg");
        var cookies = categoryService.create("Печенье", "/images/categories/cookies.jpg");

        seedCoffee(coffee);
        seedTea(tea);
        seedCold(cold);
        seedPastries(pastries);
        seedCookies(cookies);

        seedVenues();

        log.info("Тестовые данные загружены.");
    }

    private void seedCoffee(Category cat) {
        var cappuccino = product(cat, "Капучино", "Двойной эспрессо с воздушной молочной пенкой", 280,
                "https://images.unsplash.com/photo-1572442388796-11668a67e53d?w=600&h=600&fit=crop");
        param(cappuccino, "Размер", List.of(val("200 мл"), val("300 мл", 50), val("400 мл", 100)));
        param(cappuccino, "Молоко", List.of(val("Обычное"), val("Овсяное", 70), val("Кокосовое", 70), val("Миндальное", 70)));
        addon(cappuccino, "Ванильный сироп", 50);
        addon(cappuccino, "Карамельный сироп", 50);
        addon(cappuccino, "Лесной орех", 50);
        addon(cappuccino, "Двойной эспрессо", 60);

        var latte = product(cat, "Латте", "Эспрессо с молоком и нежной пенкой", 310,
                "https://images.unsplash.com/photo-1570968915860-54d5c301fa9f?w=600&h=600&fit=crop");
        param(latte, "Размер", List.of(val("300 мл"), val("400 мл", 50), val("500 мл", 100)));
        param(latte, "Молоко", List.of(val("Обычное"), val("Овсяное", 70), val("Миндальное", 70)));
        addon(latte, "Ванильный сироп", 50);
        addon(latte, "Карамельный топпинг", 40);

        var americano = product(cat, "Американо", "Эспрессо, разбавленный горячей водой", 200,
                "https://images.unsplash.com/photo-1551030173-122aabc4489c?w=600&h=600&fit=crop");
        param(americano, "Размер", List.of(val("200 мл"), val("300 мл", 50), val("400 мл", 100)));
        param(americano, "Температура", List.of(val("Горячий"), val("Холодный")));
        addon(americano, "Молоко", 40);
        addon(americano, "Сливки", 40);

        var flatWhite = product(cat, "Флэт Уайт", "Двойной ристретто с бархатным молоком", 290,
                "https://images.unsplash.com/photo-1577968897966-3d4325b36b61?w=600&h=600&fit=crop");
        param(flatWhite, "Молоко", List.of(val("Обычное"), val("Овсяное", 70)));
        addon(flatWhite, "Ванильный сироп", 50);

        var raf = product(cat, "Раф", "Эспрессо со сливками и ванильным сахаром", 350,
                "https://images.unsplash.com/photo-1485808191679-5f86510681a2?w=600&h=600&fit=crop");
        param(raf, "Размер", List.of(val("300 мл"), val("400 мл", 50)));
        param(raf, "Вкус", List.of(val("Классический"), val("Пряный"), val("Солёная карамель"), val("Апельсин")));
        addon(raf, "Двойной эспрессо", 60);

        var espresso = product(cat, "Эспрессо", "Классический чёрный кофе", 150,
                "https://images.unsplash.com/photo-1510707577719-ae7c14805e3a?w=600&h=600&fit=crop");
        param(espresso, "Объём", List.of(val("Одиночный"), val("Двойной", 50), val("Тройной", 100)));

        var macchiato = product(cat, "Маккиато", "Эспрессо с каплей молочной пены", 230,
                "https://images.unsplash.com/photo-1594631252845-29fc4cc8cde9?w=600&h=600&fit=crop");
        param(macchiato, "Размер", List.of(val("200 мл"), val("300 мл", 50)));
        addon(macchiato, "Ванильный сироп", 50);

        var mocha = product(cat, "Мокко", "Эспрессо с шоколадом и молоком", 340,
                "https://images.unsplash.com/photo-1578314675249-a6910f80cc4e?w=600&h=600&fit=crop");
        param(mocha, "Размер", List.of(val("300 мл"), val("400 мл", 50)));
        param(mocha, "Молоко", List.of(val("Обычное"), val("Овсяное", 70)));
        addon(mocha, "Взбитые сливки", 60);
        addon(mocha, "Маршмеллоу", 40);
    }

    private void seedTea(Category cat) {
        var greenTea = product(cat, "Зелёный чай", "Классический листовой зелёный чай", 180,
                "https://images.unsplash.com/photo-1556881286-fc6915169721?w=600&h=600&fit=crop");
        param(greenTea, "Объём", List.of(val("300 мл"), val("500 мл", 50)));
        param(greenTea, "Температура", List.of(val("Горячий"), val("Холодный с льдом")));
        addon(greenTea, "Лимон", 30);
        addon(greenTea, "Мёд", 40);

        var blackTea = product(cat, "Чёрный чай", "Крепкий листовой чёрный чай", 170,
                "https://images.unsplash.com/photo-1597318181409-cf64d0b5d8a2?w=600&h=600&fit=crop");
        param(blackTea, "Объём", List.of(val("300 мл"), val("500 мл", 50)));
        addon(blackTea, "Молоко", 40);
        addon(blackTea, "Лимон", 30);
        addon(blackTea, "Мёд", 40);

        var matcha = product(cat, "Матча латте", "Японский чай матча на молоке", 320,
                "https://images.unsplash.com/photo-1536256263959-770b48d82b0a?w=600&h=600&fit=crop");
        param(matcha, "Размер", List.of(val("300 мл"), val("400 мл", 50)));
        param(matcha, "Молоко", List.of(val("Обычное"), val("Овсяное", 70), val("Кокосовое", 70)));
        addon(matcha, "Ванильный сироп", 50);

        var chamomile = product(cat, "Ромашковый чай", "Травяной чай из цветков ромашки", 190,
                "https://images.unsplash.com/photo-1576092768241-dec231879fc3?w=600&h=600&fit=crop");
        param(chamomile, "Объём", List.of(val("300 мл"), val("500 мл", 50)));
        addon(chamomile, "Мёд", 40);

        var hibiscus = product(cat, "Каркаде", "Цветочный чай из гибискуса", 200,
                "https://images.unsplash.com/photo-1544787219-7f47ccb76574?w=600&h=600&fit=crop");
        param(hibiscus, "Температура", List.of(val("Горячий"), val("Холодный")));
        addon(hibiscus, "Лимон", 30);
    }

    private void seedCold(Category cat) {
        var lemonade = product(cat, "Лимонад классический", "Освежающий домашний лимонад", 250,
                "https://images.unsplash.com/photo-1621263764928-df1444c5e859?w=600&h=600&fit=crop");
        param(lemonade, "Объём", List.of(val("300 мл"), val("500 мл", 50)));
        addon(lemonade, "Мята", 30);
        addon(lemonade, "Имбирь", 30);

        var icedLatte = product(cat, "Айс латте", "Холодный латте со льдом", 310,
                "https://images.unsplash.com/photo-1517701550927-30cf4ba1dba5?w=600&h=600&fit=crop");
        param(icedLatte, "Размер", List.of(val("300 мл"), val("400 мл", 50)));
        param(icedLatte, "Молоко", List.of(val("Обычное"), val("Овсяное", 70), val("Кокосовое", 70)));
        addon(icedLatte, "Ванильный сироп", 50);
        addon(icedLatte, "Карамельный сироп", 50);

        var coldBrew = product(cat, "Колд брю", "Кофе холодного заваривания", 290,
                "https://images.unsplash.com/photo-1461023058943-07fcbe16d735?w=600&h=600&fit=crop");
        param(coldBrew, "Объём", List.of(val("300 мл"), val("400 мл", 50)));
        addon(coldBrew, "Молоко", 40);
        addon(coldBrew, "Сироп на выбор", 50);

        var smoothie = product(cat, "Смузи клубника-банан", "Густой фруктовый смузи", 350,
                "https://images.unsplash.com/photo-1505252585461-04db1eb84625?w=600&h=600&fit=crop");
        param(smoothie, "Объём", List.of(val("300 мл"), val("500 мл", 50)));
        addon(smoothie, "Семена чиа", 40);
        addon(smoothie, "Протеин", 80);

        var lemonadeMint = product(cat, "Лимонад мята-огурец", "Лимонад с мятой и свежим огурцом", 260,
                "https://images.unsplash.com/photo-1556679343-c7306c1976bc?w=600&h=600&fit=crop");
        param(lemonadeMint, "Объём", List.of(val("300 мл"), val("500 мл", 50)));
    }

    private void seedPastries(Category cat) {
        product(cat, "Десерт морковный", "Нежный морковный десерт с кремом", 328,
                "https://yastatic.net/avatars/get-grocery-goods/2998515/b45a631c-6b03-4f55-9801-4ce0a79e0fb3/600x600");

        product(cat, "Пирожное шоколадное", "Классическое шоколадное пирожное", 199,
                "https://yastatic.net/avatars/get-grocery-goods/2888787/5ba3916c-4f50-4dee-8230-5fd7f90b5c94/600x600");

        product(cat, "Меренга Французская с клубникой", "Воздушная меренга с клубничным кремом", 167,
                "https://yastatic.net/avatars/get-grocery-goods/2888787/caa7c89e-c401-4513-b7f2-c40c4a5482b3/600x600");

        product(cat, "Пирожное морковное", "Пирожное из моркови с сырным кремом", 171,
                "https://yastatic.net/avatars/get-grocery-goods/15712601/ddbea934-7e1b-4c1d-8f43-aed97d9ba721/600x600");

        product(cat, "Пирожное Суфле в шоколаде", "Нежное суфле в шоколадной глазури", 101,
                "https://yastatic.net/avatars/get-grocery-goods/2783132/897659fe-0e63-470d-9f8d-d07922c6df60/600x600");

        product(cat, "Тирамису", "Итальянский десерт с маскарпоне и кофе", 146,
                "https://yastatic.net/avatars/get-grocery-goods/2791769/11c3dd4f-47f8-4200-aca1-1f802056e041/600x600");

        product(cat, "Шу вишня", "Заварное пирожное с вишнёвой начинкой", 167,
                "https://yastatic.net/avatars/get-grocery-goods/2998515/934d0d62-ead0-4591-977b-11d293180c0a/600x600");

        product(cat, "Трубочка со сливками", "Хрустящая трубочка с нежными сливками", 139,
                "https://yastatic.net/avatars/get-grocery-goods/2756334/8a463526-c76d-446f-9a41-b4ecfa3cf614/600x600");

        product(cat, "Пирожное Вупи куки", "Два шоколадных печенья с кремовой прослойкой", 164,
                "https://yastatic.net/avatars/get-grocery-goods/2783132/a7cd7eeb-5f8b-4ca1-ad3f-cd6c0bff8cd2/600x600");

        product(cat, "Шоколадная колбаса", "Десерт из печенья с шоколадом и орехами", 220,
                "https://yastatic.net/avatars/get-grocery-goods/5416507/5e564464-d7d0-4f93-9fee-8c33bfe00f4e/600x600");
    }

    private void seedCookies(Category cat) {
        product(cat, "Печенье Творожное", "Домашнее печенье из творожного теста", 248,
                "https://yastatic.net/avatars/get-grocery-goods/2998515/976a190c-fd30-48ff-8fbb-fdd0ddeb7d05/600x600");

        product(cat, "Печенье Орешек со сгущёнкой", "Орешки с варёной сгущёнкой", 360,
                "https://yastatic.net/avatars/get-grocery-goods/2750890/b38e3cb9-9cfc-470f-90a6-08561eedbcf9/600x600");

        product(cat, "Пряники на ёлку", "Имбирные пряники с глазурью", 230,
                "https://yastatic.net/avatars/get-grocery-goods/2783132/c293f6c1-30c8-4d41-9ad9-f560e5a98f3d/600x600");
    }

    private void seedVenues() {
        venueService.create("Кофейня на Тверской", "ул. Тверская, 15, Москва", 55.7653, 37.6059);
        venueService.create("Кофейня на Арбате", "ул. Арбат, 22, Москва", 55.7522, 37.5905);
        venueService.create("Кофейня у парка", "Кутузовский пр-т, 41, Москва", 55.7425, 37.5605);
    }

    private Product product(Category category, String name, String description, int price, String imageUrl) {
        return productService.create(category.getId(), name, description, price, imageUrl);
    }

    private void param(Product product, String name, List<ParameterValue> values) {
        parameterService.create(product.getId(), name, values);
    }

    private ParameterValue val(String value, int priceMod) {
        return ParameterValue.of(value, priceMod);
    }

    private ParameterValue val(String value) {
        return ParameterValue.of(value);
    }

    private void addon(Product product, String name, int price) {
        addonService.create(product.getId(), name, price);
    }
}

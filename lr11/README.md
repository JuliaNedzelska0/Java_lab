# Лабораторна робота №11. Hibernate ORM (Jakarta Persistence), високий рівень

**Предметна область:** квитки на транспорт і пасажири (продовження ЛР №6–10).
**Гілка:** `lr11/hibernate`
**Основа:** проєкт з ЛР №10 (Maven, H2, `init.sql`, пул HikariCP). Гілку краще створювати від `lr10` або після мерджу PR ЛР №10 у `main`.

У ЛР №7 і №10 були реалізації `CRUDService<T>`, `TicketService`, `PassengerService` на колекціях і на JDBC. Тут третя реалізація: на Hibernate (`HibernateTicketService`, `HibernatePassengerService`). Секції 1–7 в `Main` (демо з ЛР №7) викликають сервіс лише через інтерфейс і не змінились, змінюється тільки рядок, де створюється реалізація.

## Запуск
Потрібні JDK 21 і Maven 3.9+. БД створювати не треба: H2 створює файл `data/tickets.mv.db`, а схему при старті виконує `init.sql` (як у ЛР №10).
```bash
mvn compile
mvn exec:java
```
Щоб почати з порожньої БД, видаліть папку `data/`. Параметри підключення лежать в `application.properties` (їх можна перевизначити `DB_URL`, `DB_USER`, `DB_PASSWORD`). Hibernate не має власного підключення: `JpaUtil` передає йому `DataSource` з `DBConnection`.

## Залежності
| Залежність | scope | Навіщо |
|---|---|---|
| `org.hibernate.orm:hibernate-core:7.4.2.Final` | compile | ORM; транзитивно приносить `jakarta.persistence-api` |
| `com.zaxxer:HikariCP:5.1.0` | compile | пул з'єднань (з ЛР №10) |
| `com.h2database:h2:2.3.232` | runtime | JDBC-драйвер (з ЛР №10) |
| `org.slf4j:slf4j-simple:2.0.16` | runtime | логування; рівень `warn` у `simplelogger.properties` |

## Конфігурація (`META-INF/persistence.xml`)
Один persistence-unit `ticketsPU`. Файл лежить у `META-INF`, бо за цим шляхом `Persistence.createEntityManagerFactory` шукає конфігурацію в classpath. Налаштовано:
- `hibernate.hbm2ddl.auto=validate`: Hibernate нічого не створює, а лише перевіряє, що сутності відповідають таблицям з `init.sql`. `update` або `create` у реальному проєкті небезпечні: вони змінюють схему без контролю й можуть знищити дані.
- `hibernate.show_sql=true` і `hibernate.format_sql=true`: видно кожен SQL.
- `hibernate.generate_statistics=true`: рахує SQL-запити (для демо N+1).
- Діалект явно не вказано: сучасний Hibernate визначає його за метаданими підключення.

**`JpaUtil` (Singleton).** `EntityManagerFactory` важкий (розбирає мапінг, перевіряє схему), тому створюється один раз у holder-класі. `EntityManager` легкий і не потокобезпечний, тому створюється на кожну операцію й закривається. Методи: `read(...)` для читання, `inTransaction(...)` з commit і rollback при винятку.

## Сутності
| Клас | Таблиця | Примітка |
|---|---|---|
| `BaseEntity` | немає | `@MappedSuperclass`: `id` (`@Id`) і `name` (`NOT NULL`, `length=200`) |
| `Passenger` | `passengers` | `age`, колекція `tickets` |
| `Ticket` (абстрактний) | `tickets` | `@Inheritance(SINGLE_TABLE)`, дискримінатор `ticket_type` |
| `TrainTicket` / `SleeperTrainTicket` / `BusTicket` | `tickets` | значення `TRAIN` / `SLEEPER` / `BUS`, стовпці `carriage`, `upper_berth`, `platform` |

Що змінилось у моделі порівняно з ЛР №7–10: прибрано `final` і `sealed` (Hibernate потрібні нефінальні класи та конструктор без параметрів, у нас `protected`), поля стали нефінальними (Hibernate заповнює їх рефлексією), ціна в `Ticket` це `BigDecimal` (`NUMERIC(10,2)`), метод `getPrice()` лишився `double` для `Payable` і знижок. Замість `passengerId` тепер посилання `Ticket.passenger`.

- **Ключ без `@GeneratedValue`.** Номер квитка й пасажира задає домен (як у `BaseEntity` з ЛР №7), а в `init.sql` немає автоінкременту. Тому `save` виконує `merge`: він сам вирішує між `INSERT` і `UPDATE`.
- **`@Column`** (`nullable`, `length`, `precision`, `scale`) повторює `init.sql`. Схему створює не Hibernate, а ці значення використовує `validate` для перевірки типу й наявності колонки та вони документують схему в коді.
- **`@Enumerated(EnumType.STRING)`** для `TicketStatus`: в БД лежить `BOOKED`, а не 0. З `ORDINAL` додавання значення в середину enum тихо змінило б зміст уже збережених даних.
- **`@Transient`** на списку спостерігачів `observers`: це стан у пам'яті, у БД його немає.
- **SINGLE_TABLE** обрано, бо `init.sql` вже має одну таблицю `tickets`, а підкласи відрізняються лише 1–2 полями. Мінус: стовпці підкласів допускають `NULL`. `JOINED` дав би нормалізовані таблиці, але JOIN на кожне читання; `TABLE_PER_CLASS` ускладнює поліморфні запити.
- **`equals`/`hashCode`** за `id` (як у ЛР №7). Ключ задається до збереження й не змінюється, тому `hashCode` стабільний, а квиток №5 і пасажир №5 не рівні. Замість `getClass()` порівняння типів іде через `Hibernate.getClass`, бо ліниве посилання може бути проксі-підкласом.
- **`toString`** у `Passenger` і `Ticket` не виводить зв'язану сторону: інакше пасажир друкував би квитки, а квиток пасажира, і вийшла б нескінченна рекурсія.

## Зв'язок «один до багатьох» (рівень 2)
`Passenger` 1 — N `Ticket`, двоспрямований.

- **Власник зв'язку: `Ticket.passenger` (`@ManyToOne` + `@JoinColumn(name="passenger_id")`).** Зовнішній ключ фізично живе в таблиці `tickets`, а Hibernate записує в БД значення саме з боку, де цей стовпець. Бік `Passenger.tickets` (`@OneToMany(mappedBy="passenger")`) лише відображення і нічого не записує.
- **Допоміжні методи** `addTicket`/`removeTicket` змінюють обидві сторони: без `ticket.setPassenger(...)` власник не оновився б, і в БД `passenger_id` лишився б `NULL`. У демо (секція 8) видно, що після `removeTicket` треба зберегти саме квиток: FK змінює власник.
- **`FetchType.LAZY`** на обох сторонах. `@ManyToOne` за замовчуванням EAGER, тому його вказано явно. Інакше кожне читання квитка тягнуло б пасажира, а пасажира його квитки.
- **`cascade = {PERSIST, MERGE}`** на `Passenger.tickets`: збереження пасажира з новими квитками зберігає й квитки одним викликом. **`REMOVE` немає:** квитки це фінансові документи, а в БД стоїть `ON DELETE RESTRICT`, тож видалення пасажира з квитками має бути помилкою, а не мовчазним видаленням. На стороні `@ManyToOne` каскаду `REMOVE` немає зовсім: видалення квитка не повинно видаляти пасажира, що має інші квитки.
- **`orphanRemoval` вимкнено.** Квиток, прибраний з колекції, не має зникати з БД: поле `passenger_id` допускає `NULL`, і квиток просто стає нікому не призначеним. Якби вимагалося, щоб квиток без власника не існував, тоді `orphanRemoval = true`; воно відрізняється від `REMOVE` тим, що видаляє дочірній запис при відриві від батька, а не при видаленні батька.

## JPQL (рівень 3)
Усі запити JPQL, значення тільки через іменовані параметри (`:name`, `:city`, `:id`), без склеювання значень. До тексту запиту підставляються лише константи з коду (назва сутності, `order by`).
- Сортування: `findAllSortedByName`, `findAllSortedByNameAndPrice` (`order by e.name, e.price, e.id`), `findAllSortedByNameAndAge`. Українське впорядкування літер забезпечує `SET COLLATION UKRAINIAN` в `init.sql` (специфічно для H2).
- Пошук за полем: `findByName`, `findByCityOfDeparture`.
- Сторінка: `findPage(page, size)` через `setFirstResult` і `setMaxResults` з `order by id`.

### LazyInitializationException
`findById` повертає пасажира після закриття `EntityManager` (він detached), колекція `tickets` ще не завантажена. Спроба `getTickets().size()` кидає `LazyInitializationException`: звернення до лінивої колекції потребує відкритої сесії. Переведення зв'язку на EAGER лише приховує проблему: кожне читання почало б тягнути зайві дані. Виправлення: завантажити колекцію в запиті, коли вона справді потрібна (`findByIdWithTickets`: `left join fetch p.tickets`), або звертатись до неї, поки сесія відкрита.

### Проблема N+1
Запит усіх пасажирів і обхід `getTickets()` у циклі: один `SELECT` пасажирів і ще по одному `SELECT` квитків на кожного. На даних демо (6 пасажирів) це **1 + 6 = 7** запитів проти **1** із `JOIN FETCH` (`select distinct p from Passenger p left join fetch p.tickets`). `Main` виводить фактичні числа з `Statistics.getPrepareStatementCount()`. Для 1000 пасажирів різниця 1001 проти 1.

Обмеження `JOIN FETCH` разом з пагінацією: рядки результату дублюються на кожен дочірній запис, тож `LIMIT` по рядках не дорівнює ліміту по пасажирах. Hibernate тоді завантажує все й ріже сторінку в пам'яті (з попередженням), тобто втрачається сенс пагінації. Рішення: спершу вибрати id сторінки батьків, потім окремим запитом `join fetch ... where p.id in :ids`.

## Перевірка `validate`
1. У `Passenger` змініть `@Column(name = "age", ...)` на `@Column(name = "years", ...)`.
2. Запустіть `mvn exec:java`: старт зупиняється, Hibernate скаржиться на розбіжність, у повідомленні буде `Schema-validation: missing column [years] in table [passengers]`.
3. Поверніть `age`.

`validate` ловить відсутні таблиці й колонки та різні типи колонок, але не помічає, що `length` в анотації менший чи більший за `VARCHAR(n)` в БД, тож розбіжність довжини лишиться непоміченою.

## Структура
```
src/main/java/ua/edu/tickets/            модель-сутності, інтерфейси, реалізація в пам'яті (ЛР №7), Main
src/main/java/ua/edu/tickets/hibernate/  JpaUtil, HibernateCrudService, Hibernate*Service, ProblemsDemo
src/main/java/ua/edu/tickets/jdbc/       DBConnection (пул) і SchemaInitializer (init.sql) з ЛР №10
src/main/resources/META-INF/persistence.xml
src/main/resources/init.sql, application.properties
```
JDBC-реалізація сервісів лишилась у ЛР №10 і тут не потрібна: Hibernate замінює її.

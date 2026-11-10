# Лабораторна робота №5. Рефакторинг із застосуванням паттернів (рівень 3)

**Предметна область:** квитки зі знижками (рефакторинг коду ЛР №4).
**Гілка:** `lr5/tickets-patterns`

## Запуск
Потрібен JDK 11+.
```bash
mkdir out
javac -encoding UTF-8 -d out *.java
java -Dfile.encoding=UTF-8 -cp out Main
```

## Рівень 1. Factory
**Проблема.** У `Main` знижки створювались через `new` у кількох місцях (`new StudentDiscount()` в масиві та в `priceWith`). Новий тип знижки потрібно було додавати в кожне таке місце.

**Рішення.** `DiscountFactory.create(String type)` повертає `DiscountStrategy` за кодом (`none`, `student`, `pensioner`). Клієнт працює лише з інтерфейсом.

Невідомий тип -> `IllegalArgumentException`, щоб помилка в коді (наприклад, `"studnet"`) не ховалась.

Новий тип знижки: новий клас + один `case` у фабриці. `Main` і `Ticket` не змінюються.

## Рівень 2. Observer
**Проблема.** Зміна статусу квитка має сповіщати інші частини системи, але ця логіка не повинна жити в `Ticket`.

**Рішення.** `Ticket` -- Subject (`subscribe`, `unsubscribe`, `setStatus`). Інтерфейс `TicketObserver`, два спостерігачі: `LoggerObserver` (лог) і `NotificationObserver` (SMS). Вони не знають один про одного.

Новий спостерігач: один новий клас, `Ticket` не змінюється.

## Рівень 3. Adapter
**Проблема.** Для SMS потрібен зовнішній сервіс `ExternalSmsGateway` з іншим API: метод `pushMessage(text, priority)`, а наш код очікує `NotificationSender.send(message)`. Змінювати чужий клас не можна.

**Рішення.** `SmsGatewayAdapter implements NotificationSender` містить посилання на `ExternalSmsGateway` (композиція) і перекладає `send(message)` у `pushMessage(message, 1)`. `NotificationObserver` працює лише з інтерфейсом `NotificationSender`.

**Чому Adapter, а не переписування класу.** `ExternalSmsGateway` -- сторонній код (бібліотека), його не можна або небезпечно змінювати. Адаптер ізолює несумісність в одному місці: при заміні сервісу змінюється лише адаптер.

Новий сервіс (наприклад, e-mail): новий адаптер, `NotificationObserver` і `Ticket` не змінюються.

## Перевірка
Вивід перших секцій `Main` збігається з ЛР №4, нова поведінка додана лише в кінець.

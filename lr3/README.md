# ЛР №3. Обробка винятків

Предметна область: квитки (продовження ЛР №2).

## Що реалізовано
- **Рівень 1:** `try-catch-finally`; `InputMismatchException` (некоректне введення), `NegativeArraySizeException` (від'ємна кількість квитків); `finally` закриває `Scanner`.
- **Рівень 2:** checked-винятки `InvalidPriceException` та `InvalidPlaceException` з полем некоректного значення; кидаються в конструкторі `Ticket`.
- **Рівень 3:** кілька `catch` від специфічних до загальних; re-throw у `readTicket`; ієрархія `TicketException` → `InvalidPriceException`, `InvalidPlaceException`.

## Запуск
```
cd lr3
javac *.java
java Main
```

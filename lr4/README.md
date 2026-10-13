# ЛР №4. Програмні інтерфейси в Java

Предметна область: квитки на автобус (продовження ЛР №1-3).

## Що реалізовано
- **Рівень 1:** інтерфейс `DiscountStrategy` (розрахунок ціни зі знижкою); реалізації `NoDiscount`,
  `StudentDiscount`, `PensionerDiscount`; у `Main` масив `DiscountStrategy[]` і виклик методів у циклі.
- **Рівень 2:**
  - `default`-метод `describe()` в `DiscountStrategy`, перевизначений у `PensionerDiscount`.
  - Абстрактний клас `AbstractPercentDiscount` зі спільною формулою відсоткової знижки.
    `NoDiscount` реалізує інтерфейс напряму, бо спільної логіки немає.
  - `Ticket` реалізує два інтерфейси: `Comparable<Ticket>` (сортування) і `Payable` (бізнес-поведінка).

## Запуск
```
cd lr4
javac *.java
java Main
```

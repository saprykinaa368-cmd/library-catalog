package ru.library.model;

public record Shelf(int number, Cabinet cabinet) implements Addressable {

    /**
     * Возвращает полный строковый адрес местоположения полки.
     * @return Адрес в формате "Комната → Шкаф → Полка X"
     */
    @Override
    public String getFullAddress() {
        return "%s → %s → Полка %d".formatted(cabinet.room().name(), cabinet.name(), number);
    }
}
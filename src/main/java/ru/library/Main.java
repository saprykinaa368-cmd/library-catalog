package ru.library;

import ru.library.model.*;
import ru.library.service.LibraryCatalog;
import ru.library.exception.BookAlreadyIssuedException;

import java.time.LocalDate;
import java.util.Set;

public class Main {
    public static void main(String[] args) {
        var catalog = new LibraryCatalog();

        // 1. Создание мест хранения
        var room = new Room("Библиотека");
        var cabinet = new Cabinet("Шкаф Фантастики", room);
        var shelf = new Shelf(1, cabinet);

        var author = new Author("Стивен Кинг");
        var book = new Book("978-5-17", "Темная Башня", author, Set.of(Genre.FANTASY, Genre.FICTION));

        // 2. Наполнение модели (Генерируем 35 объектов BookCopy для сдачи)
        System.out.println("Генерация 35 экземпляров книг...");
        BookCopy firstCopy = null;
        for (int i = 1; i <= 35; i++) {
            var copy = new BookCopy("INV-00" + i, book, shelf, Condition.NEW);
            catalog.addCopy(copy);
            if (i == 1) firstCopy = copy;
        }
        System.out.println("Книги успешно добавлены в каталог.\n");

        // 3. Поиск
        System.out.println("--- Базовый уровень: Поиск ---");
        var foundBooks = catalog.searchBooks("башня", Genre.FANTASY);
        foundBooks.forEach(b -> System.out.println("Найдена книга: " + b.title() + " (" + b.isbn() + ")"));

        // 4. Где лежит книга
        System.out.println("\nГде лежит книга INV-001: " + catalog.locateCopy(firstCopy));

        // 5. Выдача
        System.out.println("\n--- Базовый уровень: Выдача ---");
        catalog.issueCopy(firstCopy, LocalDate.now(), 14);
        System.out.println("Выдали экземпляр INV-001. Новый статус: " + catalog.locateCopy(firstCopy));

        // 6. Исключение (выдача уже выданного)
        System.out.println("\n--- Базовый уровень: Исключение ---");
        try {
            catalog.issueCopy(firstCopy, LocalDate.now(), 7);
        } catch (BookAlreadyIssuedException e) {
            System.out.println("Успешно поймано исключение: " + e.getMessage());
        }

        // 7. Списки выданных и просроченных
        System.out.println("\n--- Базовый уровень: Отчеты на сегодня ---");
        var issuedToday = catalog.getIssuedOnDate(LocalDate.now());
        System.out.println("Выдано на сегодня: " + issuedToday.size() + " шт.");

        var overdueToday = catalog.getOverdueOnDate(LocalDate.now());
        System.out.println("Просрочено на сегодня: " + overdueToday.size() + " шт.");

        // 8. Возврат
        catalog.returnCopy(firstCopy);
        System.out.println("\nКнига возвращена. Текущее местоположение: " + catalog.locateCopy(firstCopy));
    }
}
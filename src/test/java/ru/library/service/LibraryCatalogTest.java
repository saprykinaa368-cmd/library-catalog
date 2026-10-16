package ru.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.library.exception.BookAlreadyIssuedException;
import ru.library.model.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class LibraryCatalogTest {

    private LibraryCatalog catalog;
    private BookCopy copy1;
    private BookCopy copy2;
    private Book book1;
    private Book book2;

    @BeforeEach
    void setUp() {
        catalog = new LibraryCatalog();
        Room room = new Room("Читальный зал");
        Cabinet cabinet = new Cabinet("Шкаф 1", room);
        Shelf shelf = new Shelf(1, cabinet);

        Author author1 = new Author("Джошуа Блох");
        book1 = new Book("111", "Effective Java", author1, Set.of(Genre.PROGRAMMING));
        copy1 = new BookCopy("INV-01", book1, shelf, Condition.NEW);

        Author author2 = new Author("Джордж Оруэлл");
        book2 = new Book("222", "1984", author2, Set.of(Genre.FICTION));
        copy2 = new BookCopy("INV-02", book2, shelf, Condition.GOOD);

        catalog.addCopy(copy1);
        catalog.addCopy(copy2);
    }

    // 1. Поиск по названию (точное совпадение)
    @Test
    void searchBooksByExactTitle() {
        List<Book> result = catalog.searchBooks("Effective Java", null);
        assertEquals(1, result.size());
        assertEquals("Effective Java", result.getFirst().title());
    }

    // 2. Поиск по автору (частичное совпадение, игнор регистра)
    @Test
    void searchBooksByAuthorPart() {
        List<Book> result = catalog.searchBooks("оруэлл", null);
        assertEquals(1, result.size());
        assertEquals("1984", result.getFirst().title());
    }

    // 3. Поиск по жанру
    @Test
    void searchBooksByGenre() {
        List<Book> result = catalog.searchBooks("", Genre.PROGRAMMING);
        assertEquals(1, result.size());
        assertEquals(book1, result.getFirst());
    }

    // 4. Поиск несуществующей книги
    @Test
    void searchBooksNotFound() {
        List<Book> result = catalog.searchBooks("Гарри Поттер", null);
        assertTrue(result.isEmpty());
    }

    // 5. Поиск с возвратом копии списка (проверка инкапсуляции)
    @Test
    void searchBooksReturnsImmutableList() {
        List<Book> result = catalog.searchBooks("Java", null);
        assertThrows(UnsupportedOperationException.class, () -> result.add(book2));
    }

    // 6. Проверка адреса книги на полке
    @Test
    void locateCopyOnShelf() {
        String address = catalog.locateCopy(copy1);
        assertEquals("Читальный зал → Шкаф 1 → Полка 1", address);
    }

    // 7. Успешная выдача книги и изменение ее локации
    @Test
    void issueCopySuccess() {
        catalog.issueCopy(copy1, LocalDate.now(), 14);
        String address = catalog.locateCopy(copy1);
        assertTrue(address.contains("Выдана на руки"));
    }

    // 8. Исключение при выдаче уже выданной книги (Граничный случай)
    @Test
    void issueAlreadyIssuedCopyThrowsException() {
        catalog.issueCopy(copy1, LocalDate.now(), 14);
        assertThrows(BookAlreadyIssuedException.class, () ->
                catalog.issueCopy(copy1, LocalDate.now(), 7)
        );
    }

    // 9. Возврат книги
    @Test
    void returnCopySuccess() {
        catalog.issueCopy(copy1, LocalDate.now(), 14);
        catalog.returnCopy(copy1);
        assertEquals("Читальный зал → Шкаф 1 → Полка 1", catalog.locateCopy(copy1));
    }

    // 10. Список выданных на сегодня
    @Test
    void getIssuedOnDate() {
        catalog.issueCopy(copy1, LocalDate.now(), 14);
        List<Loan> issued = catalog.getIssuedOnDate(LocalDate.now());
        assertEquals(1, issued.size());
        assertEquals(copy1, issued.getFirst().copy());
    }

    // 11. Список просроченных (нет просроченных)
    @Test
    void getOverdueOnDateEmpty() {
        catalog.issueCopy(copy1, LocalDate.now(), 14);
        List<Loan> overdue = catalog.getOverdueOnDate(LocalDate.now());
        assertTrue(overdue.isEmpty());
    }

    // 12. Список просроченных (есть просроченные - граничный случай)
    @Test
    void getOverdueOnDateWithOverdue() {
        catalog.issueCopy(copy1, LocalDate.now().minusDays(20), 14); // Выдали 20 дней назад на 14 дней
        List<Loan> overdue = catalog.getOverdueOnDate(LocalDate.now());
        assertEquals(1, overdue.size());
        assertEquals(copy1, overdue.getFirst().copy());
    }
}

package ru.library.service;

import ru.library.model.*;
import ru.library.exception.BookAlreadyIssuedException;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class LibraryCatalog {
    private final List<BookCopy> allCopies = new ArrayList<>();
    private final Map<BookCopy, Loan> activeLoans = new HashMap<>();

    public void addCopy(BookCopy copy) {
        allCopies.add(copy);
    }

    /**
     * Выдает экземпляр книги на заданный срок.
     *
     * @param copy Экземпляр книги для выдачи
     * @param issueDate Дата выдачи
     * @param daysToReturn Количество дней на возврат
     * @throws BookAlreadyIssuedException если данный экземпляр уже выдан и не возвращен
     */
    public void issueCopy(BookCopy copy, LocalDate issueDate, int daysToReturn) {
        if (activeLoans.containsKey(copy)) {
            throw new BookAlreadyIssuedException("Экземпляр " + copy.getInventoryNumber() + " уже выдан!");
        }
        activeLoans.put(copy, new Loan(copy, issueDate, issueDate.plusDays(daysToReturn)));
    }

    public void returnCopy(BookCopy copy) {
        activeLoans.remove(copy);
    }

    /**
     * Определяет текущее местоположение конкретного экземпляра книги.
     *
     * @param copy Экземпляр книги
     * @return Адрес полки, либо информация о том, что книга на руках
     */
    public String locateCopy(BookCopy copy) {
        if (activeLoans.containsKey(copy)) {
            return "Выдана на руки. Срок возврата: " + activeLoans.get(copy).dueDate();
        }
        return copy.getShelf().getFullAddress();
    }

    /**
     * Ищет книги по частичному совпадению названия или автора, а также по жанру.
     *
     * @param query Строка поиска (название или автор), регистронезависима
     * @param genre Жанр для фильтрации (может быть null для поиска по всем жанрам)
     * @return Неизменяемый список найденных книг
     */
    public List<Book> searchBooks(String query, Genre genre) {
        var results = allCopies.stream()
                .map(BookCopy::getBook)
                .filter(b -> b.title().toLowerCase().contains(query.toLowerCase()) ||
                        b.author().name().toLowerCase().contains(query.toLowerCase()))
                .filter(b -> genre == null || b.genres().contains(genre))
                .distinct()
                .collect(Collectors.toList());
        return List.copyOf(results);
    }

    public List<Loan> getIssuedOnDate(LocalDate date) {
        return activeLoans.values().stream()
                .filter(loan -> !loan.issueDate().isAfter(date) && !loan.dueDate().isBefore(date))
                .toList();
    }

    public List<Loan> getOverdueOnDate(LocalDate date) {
        return activeLoans.values().stream()
                .filter(loan -> loan.dueDate().isBefore(date))
                .toList();
    }
}
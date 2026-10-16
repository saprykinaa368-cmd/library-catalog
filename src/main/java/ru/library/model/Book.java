package ru.library.model;

import java.util.Set;

/**
 * Представляет неизменяемую книгу в каталоге.
 * Ключ равенства основан на ISBN (генерируется автоматически благодаря record).
 *
 * @param isbn   Уникальный международный номер книги.
 * @param title  Название книги.
 * @param author Автор книги.
 * @param genres Множество жанров (сохраняется как неизменяемая копия).
 */
public record Book(String isbn, String title, Author author, Set<Genre> genres) {
    public Book {
        genres = Set.copyOf(genres);
    }
}
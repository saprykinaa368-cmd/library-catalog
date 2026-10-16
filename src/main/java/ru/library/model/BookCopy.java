package ru.library.model;

public class BookCopy {
    private final String inventoryNumber;
    private final Book book;
    private Shelf shelf;
    private Condition condition;

    public BookCopy(String inventoryNumber, Book book, Shelf shelf, Condition condition) {
        this.inventoryNumber = inventoryNumber;
        this.book = book;
        this.shelf = shelf;
        this.condition = condition;
    }

    public String getInventoryNumber() { return inventoryNumber; }
    public Book getBook() { return book; }
    public Shelf getShelf() { return shelf; }
    public Condition getCondition() { return condition; }
}
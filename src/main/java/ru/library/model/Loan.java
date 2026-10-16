package ru.library.model;

import java.time.LocalDate;

public record Loan(BookCopy copy, LocalDate issueDate, LocalDate dueDate) {}
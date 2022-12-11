package ru.practicum.shareit.exceptions;

public class ConflictExistsException extends RuntimeException {
    public ConflictExistsException(String message) {
        super(message);
    }
}
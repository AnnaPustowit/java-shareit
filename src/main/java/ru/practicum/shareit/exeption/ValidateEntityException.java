package ru.practicum.shareit.exeption;

public class ValidateEntityException extends RuntimeException {
    public ValidateEntityException(String message) {
        super(message);
    }
}

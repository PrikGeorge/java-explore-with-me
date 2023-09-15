package ru.practicum.exception;

public class CannotBeModifiedException extends RuntimeException {

    public CannotBeModifiedException(String message) {
        super(message);
    }

}

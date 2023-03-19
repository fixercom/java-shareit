package ru.practicum.shareit.exception;

public class EmailIsAlreadyInUseException extends RuntimeException {
    public EmailIsAlreadyInUseException(String email) {
        super(String.format("Email address %s is already used", email));
    }
}

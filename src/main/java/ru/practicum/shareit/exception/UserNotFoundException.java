package ru.practicum.shareit.exception;

public class UserNotFoundException extends RuntimeException {
    public UserNotFoundException(Long id) {
        super(String.format("There is no user with id=%d in the database", id));
    }
}

package ru.practicum.shareit.exception;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException(Long id) {
        super(String.format("There is no item with id=%d in the database", id));
    }
}

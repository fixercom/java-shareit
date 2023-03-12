package ru.practicum.shareit.exception;

public class ItemRequestNotFoundException extends RuntimeException {
    public ItemRequestNotFoundException(Long requestId) {
        super(String.format("There is no item request with id=%d in the database", requestId));
    }
}

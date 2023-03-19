package ru.practicum.shareit.exception;

public class ItemNotAvailableForBookingException extends RuntimeException {
    public ItemNotAvailableForBookingException(Long itemId) {
        super(String.format("Item with id=%s is not available for booking", itemId));
    }
}

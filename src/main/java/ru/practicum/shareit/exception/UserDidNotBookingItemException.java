package ru.practicum.shareit.exception;

public class UserDidNotBookingItemException extends RuntimeException {
    public UserDidNotBookingItemException(Long userId, Long itemId) {
        super(String.format("The user with id=%s did not book the item with id=%s", userId, itemId));
    }
}

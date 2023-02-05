package ru.practicum.shareit.exception;

public class NotOwnerItemException extends RuntimeException {
    public NotOwnerItemException(Long itemId, Long userId) {
        super(String.format("User with id=%s is not the owner of the item with id=%s", userId, itemId));
    }
}

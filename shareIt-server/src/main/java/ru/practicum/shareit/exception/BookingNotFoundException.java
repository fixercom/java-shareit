package ru.practicum.shareit.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(Long id) {
        super(String.format("There is no booking with id=%d in the database", id));
    }
}

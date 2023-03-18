package ru.practicum.shareit.exception;

import ru.practicum.shareit.booking.entity.BookingState;

public class UnknownStateException extends RuntimeException {
    public UnknownStateException(BookingState state) {
        super(String.format("Unknown state: %s", state));
    }
}

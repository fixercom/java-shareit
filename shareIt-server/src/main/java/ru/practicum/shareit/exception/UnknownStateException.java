package ru.practicum.shareit.exception;

import ru.practicum.shareit.booking.entity.State;

public class UnknownStateException extends RuntimeException {
    public UnknownStateException(State state) {
        super(String.format("Unknown state: %s", state));
    }
}

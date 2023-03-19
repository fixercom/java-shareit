package ru.practicum.shareit.exception;

public class NotPossibleChangeBookingStatusException extends RuntimeException {
    public NotPossibleChangeBookingStatusException(Long bookingId) {
        super(String.format("The status for the booking with id=%s cannot be changed", bookingId));
    }

}

package ru.practicum.shareit.exception;

import java.time.LocalDateTime;

public class IncorrectBookingDatesException extends RuntimeException {
    public IncorrectBookingDatesException(LocalDateTime startBookingDate, LocalDateTime endBookingDate) {
        super(String.format("The end date of the booking %s cannot be earlier or equal the start date %s",
                endBookingDate, startBookingDate));
    }
}

package ru.practicum.shareit.exception;

import java.time.LocalDateTime;

public class BookingEndDateBeforeStartDateException extends RuntimeException {
    public BookingEndDateBeforeStartDateException(LocalDateTime startBookingDate, LocalDateTime endBookingDate) {
        super(String.format("The end date of the booking %s cannot be earlier than the start date %s",
                endBookingDate, startBookingDate));
    }
}

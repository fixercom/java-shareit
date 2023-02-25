package ru.practicum.shareit.booking.dto;

import lombok.Data;
import ru.practicum.shareit.validation.groups.OnCreate;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import java.time.LocalDateTime;

@Data
public class BookingDtoRequest {
    private Long itemId;
    @FutureOrPresent(groups = OnCreate.class, message = "The start" +
            " date of the booking must be in the future or present")
    private LocalDateTime start;
    @Future(groups = OnCreate.class, message = "The end date of the booking must be in the future")
    private LocalDateTime end;
}

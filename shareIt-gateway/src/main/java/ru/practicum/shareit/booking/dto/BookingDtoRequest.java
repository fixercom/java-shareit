package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.groups.OnCreate;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDtoRequest {
    private Long itemId;
    @NotNull(groups = OnCreate.class, message = "The start date of the booking must not be null")
    @FutureOrPresent(groups = OnCreate.class, message = "The start" +
            " date of the booking must be in the future or present")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;
    @NotNull(groups = OnCreate.class, message = "The end date of the booking must not be null")
    @Future(groups = OnCreate.class, message = "The end date of the booking must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;
}

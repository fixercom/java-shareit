package ru.practicum.shareit.booking.entity;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;

@Data
@Builder
public class Booking {
    private Long id;
    private LocalDateTime start;
    private LocalDateTime end;
    private Item item;
    private User booker;
    private BookingStatus bookingStatus;
}

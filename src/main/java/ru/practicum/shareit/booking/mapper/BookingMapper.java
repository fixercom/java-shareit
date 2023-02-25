package ru.practicum.shareit.booking.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

@Mapper
public interface BookingMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "status", ignore = true)
    Booking toBooking(BookingDtoRequest bookingDtoRequest, Item item, User booker);

    BookingDtoResponse toBookingDtoResponse(Booking booking);

    @Mapping(target = "bookerId", source = "booker.id")
    BookingDtoForItem toBookingDtoForItem(Booking booking);

    List<BookingDtoResponse> toBookingDtoResponseList(List<Booking> bookings);
}

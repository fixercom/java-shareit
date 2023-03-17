package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.entity.State;

import java.util.List;

public interface BookingService {
    BookingDtoResponse createBooking(BookingDtoRequest bookingDtoRequest, Long userId);

    BookingDtoResponse getBookingById(Long id, Long userId);

    List<BookingDtoResponse> getAllByBookerId(Long userId, State state, Integer from, Integer size);

    List<BookingDtoResponse> getAllByItemOwnerId(Long userId, State state, Integer from, Integer size);

    BookingDtoResponse updateBooking(Long userId, Long bookingId, Boolean approved);
}

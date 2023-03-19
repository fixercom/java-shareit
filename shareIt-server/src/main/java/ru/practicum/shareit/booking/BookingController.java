package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.entity.BookingState;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.util.HeaderName;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDtoResponse createBooking(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                            @RequestBody BookingDtoRequest bookingDtoRequest,
                                            HttpServletRequest request) {
        log.debug("{} request {} received: {}", request.getMethod(), request.getRequestURI(), bookingDtoRequest);
        return bookingService.createBooking(bookingDtoRequest, userId);
    }

    @GetMapping("/{bookingId}")
    public BookingDtoResponse getBookingById(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                             @PathVariable Long bookingId,
                                             HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping()
    public List<BookingDtoResponse> getAllByBookerId(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                                     @RequestParam(defaultValue = "ALL") BookingState state,
                                                     @RequestParam(defaultValue = "0") Integer from,
                                                     @RequestParam(defaultValue = "100") Integer size,
                                                     HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return bookingService.getAllByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDtoResponse> getAllByItemOwnerId(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                                        @RequestParam(defaultValue = "ALL") BookingState state,
                                                        @RequestParam(defaultValue = "0") Integer from,
                                                        @RequestParam(defaultValue = "100") Integer size,
                                                        HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return bookingService.getAllByItemOwnerId(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public BookingDtoResponse updateBooking(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                            @PathVariable Long bookingId,
                                            @RequestParam Boolean approved,
                                            HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return bookingService.updateBooking(userId, bookingId, approved);
    }
}
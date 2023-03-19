package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.state.BookingState;
import ru.practicum.shareit.util.HeaderName;
import ru.practicum.shareit.validation.groups.OnCreate;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Min;

@Controller
@RequestMapping("/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {
    private final BookingClient bookingClient;

    @PostMapping
    public ResponseEntity<Object> createBooking(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                                @RequestBody @Validated(OnCreate.class)
                                                BookingDtoRequest bookingDtoRequest,
                                                HttpServletRequest request) {
        log.debug("{} request {} received: {}", request.getMethod(), request.getRequestURI(), bookingDtoRequest);
        return bookingClient.createBooking(bookingDtoRequest, userId);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                                 @PathVariable Long bookingId,
                                                 HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return bookingClient.getBookingById(bookingId, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllByBookerId(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                                   @RequestParam(defaultValue = "ALL") BookingState state,
                                                   @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                   @RequestParam(defaultValue = "100") @Min(1) Integer size,
                                                   HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return bookingClient.getAllByBookerId(userId, state, from, size);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getAllByItemOwnerId(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                                      @RequestParam(defaultValue = "ALL") BookingState state,
                                                      @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                      @RequestParam(defaultValue = "100") @Min(1) Integer size,
                                                      HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return bookingClient.getAllByItemOwnerId(userId, state, from, size);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> updateBooking(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                                @PathVariable Long bookingId,
                                                @RequestParam Boolean approved,
                                                HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return bookingClient.updateBooking(userId, bookingId, approved);
    }
}

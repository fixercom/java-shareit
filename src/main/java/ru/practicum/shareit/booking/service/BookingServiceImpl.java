package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.entity.State;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional
    public BookingDtoResponse createBooking(BookingDtoRequest bookingDtoRequest, Long userId) {
        Item item = getItemById(bookingDtoRequest.getItemId());
        checkNotItemOwner(item, userId);
        User booker = getUserById(userId);
        checkBookingAvailability(item);
        Booking booking = bookingMapper.toBooking(bookingDtoRequest, item, booker);
        checkBookingDates(booking);
        booking.setStatus(BookingStatus.WAITING);
        BookingDtoResponse bookingDtoResponse = bookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
        log.debug("Booking saved in the database with id={}: {}", bookingDtoResponse.getId(), booking);
        return bookingDtoResponse;
    }

    @Override
    public BookingDtoResponse getBookingById(Long id, Long userId) {
        Booking booking = getBookingByIdWithoutCheckAccess(id);
        checkUserAccessRights(booking, userId);
        log.debug("Booking with id={} was obtained from the database: {}", id, booking);
        return bookingMapper.toBookingDtoResponse(booking);
    }

    @Override
    public List<BookingDtoResponse> getAllByBookerId(Long userId, State state, Integer from, Integer size) {
        checkUserExists(userId);
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("start").descending());
        List<Booking> bookings;
        if (state == State.ALL) {
            bookings = bookingRepository.findAllUserBookings(userId, pageable);
        } else if (state == State.PAST) {
            bookings = bookingRepository.findAllPastUserBookings(userId, LocalDateTime.now(), pageable);
        } else if (state == State.CURRENT) {
            bookings = bookingRepository.findAllCurrentUserBookings(userId, LocalDateTime.now(), pageable);
        } else if (state == State.FUTURE) {
            bookings = bookingRepository.findAllFutureUserBookings(userId, LocalDateTime.now(), pageable);
        } else if (state == State.WAITING) {
            bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.WAITING, pageable);
        } else if (state == State.REJECTED) {
            bookings = bookingRepository.findAllByBookerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
        } else {
            throw new UnknownStateException(State.UNSUPPORTED_STATUS);
        }
        log.debug("Received a list of bookings for the user with id={}", userId);
        return bookingMapper.toBookingDtoResponseList(bookings);
    }

    @Override
    public List<BookingDtoResponse> getAllByItemOwnerId(Long userId, State state, Integer from, Integer size) {
        checkUserExists(userId);
        Pageable pageable = PageRequest.of( from / size, size, Sort.by("start").descending());
        List<Booking> bookings;
        if (state == State.ALL) {
            bookings = bookingRepository.findAllItemOwnerBookings(userId, pageable);
        } else if (state == State.PAST) {
            bookings = bookingRepository.findAllPastItemOwnerBookings(userId, LocalDateTime.now(), pageable);
        } else if (state == State.CURRENT) {
            bookings = bookingRepository.findAllCurrentItemOwnerBookings(userId, LocalDateTime.now(), pageable);
        } else if (state == State.FUTURE) {
            bookings = bookingRepository.findAllFutureItemOwnerBookings(userId, LocalDateTime.now(), pageable);
        } else if (state == State.WAITING) {
            bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.WAITING, pageable);
        } else if (state == State.REJECTED) {
            bookings = bookingRepository.findAllByItemOwnerIdAndStatus(userId, BookingStatus.REJECTED, pageable);
        } else {
            throw new UnknownStateException(State.UNSUPPORTED_STATUS);
        }
        log.debug("Received a list of bookings for the user's own items for user with id={}", userId);
        return bookingMapper.toBookingDtoResponseList(bookings);
    }

    @Override
    @Transactional
    public BookingDtoResponse updateBooking(Long userId, Long bookingId, Boolean approved) {
        Booking booking = getBookingByIdWithoutCheckAccess(bookingId);
        Item item = booking.getItem();
        checkItemOwner(item, userId);
        checkBookingStatus(booking);
        changeBookingStatus(booking, approved);
        log.debug("Booking with id={} was updated", booking);
        return bookingMapper.toBookingDtoResponse(booking);
    }

    private void checkItemOwner(Item item, Long userId) {
        if (!item.getOwner().getId().equals(userId)) {
            throw new ItemNotFoundException(item.getId());
        }
    }

    private void checkNotItemOwner(Item item, Long userId) {
        if (item.getOwner().getId().equals(userId)) {
            throw new ItemNotFoundException(item.getId());
        }
    }

    private Item getItemById(Long id) {
        return itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
    }

    private User getUserById(Long id) {
        return userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
    }

    private void checkUserExists(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserNotFoundException(id);
        }
    }

    private void checkBookingAvailability(Item item) {
        if (!item.getAvailable()) {
            throw new ItemNotAvailableForBookingException(item.getId());
        }
    }

    private void checkBookingDates(Booking booking) {
        LocalDateTime startBookingDate = booking.getStart();
        LocalDateTime endBookingDate = booking.getEnd();
        if (endBookingDate.isBefore(startBookingDate)) {
            throw new BookingEndDateBeforeStartDateException(startBookingDate, endBookingDate);
        }
    }

    private Booking getBookingByIdWithoutCheckAccess(Long id) {
        return bookingRepository.findById(id).orElseThrow(() -> new BookingNotFoundException(id));
    }

    private void checkUserAccessRights(Booking booking, Long userId) {
        Long bookerId = booking.getBooker().getId();
        Long ownerId = booking.getItem().getOwner().getId();
        if (!(userId.equals(bookerId)) && !(userId.equals(ownerId))) {
            throw new BookingNotFoundException(booking.getId());
        }
    }

    private void checkBookingStatus(Booking booking) {
        if (booking.getStatus() != BookingStatus.WAITING) {
            throw new NotPossibleChangeBookingStatusException(booking.getId());
        }
    }

    private void changeBookingStatus(Booking booking, Boolean approved) {
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
    }
}

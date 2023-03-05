package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingNotFoundException;
import ru.practicum.shareit.exception.NotPossibleChangeBookingStatusException;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void updateBooking_whenBookingWasApprovedByOwner_thenReturnApprovedStatus() {
        User itemOwner = createUser(1L);
        User booker = createUser(2L);
        Item item = createItem(itemOwner);
        Booking booking = createBooking(item, booker, BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDtoResponse dtoResponse = bookingService.updateBooking(1L, 5L, true);

        assertThat(dtoResponse.getStatus()).isEqualTo(BookingStatus.APPROVED);
        verify(bookingMapper).toBookingDtoResponse(any(Booking.class));
    }

    @Test
    void updateBooking_whenBookingWasRejectedByOwner_thenReturnRejectedStatus() {
        User itemOwner = createUser(3L);
        User booker = createUser(4L);
        Item item = createItem(itemOwner);
        Booking booking = createBooking(item, booker, BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDtoResponse dtoResponse = bookingService.updateBooking(3L, 7L, false);

        assertThat(dtoResponse.getStatus()).isEqualTo(BookingStatus.REJECTED);
        verify(bookingMapper).toBookingDtoResponse(any(Booking.class));
    }

    @Test
    void updateBooking_whenUserNotItemOwner_thenThrowException() {
        User itemOwner = createUser(3L);
        User booker = createUser(4L);
        Item item = createItem(itemOwner);
        Booking booking = createBooking(item, booker, BookingStatus.WAITING);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.updateBooking(5L, 7L, false))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessage("There is no booking with id=%d in the database", booking.getId());
    }

    @Test
    void updateBooking_whenNotWaitingBookingStatus_thenThrowException() {
        User itemOwner = createUser(3L);
        User booker = createUser(4L);
        Item item = createItem(itemOwner);
        Booking booking = createBooking(item, booker, BookingStatus.REJECTED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.updateBooking(3L, 7L, false))
                .isInstanceOf(NotPossibleChangeBookingStatusException.class)
                .hasMessage("The status for the booking with id=%s cannot be changed", booking.getId());
    }


    User createUser(Long userId) {
        return User.builder()
                .id(userId)
                .name("Name")
                .email("email@email.ru")
                .build();
    }

    Item createItem(User itemOwner) {
        return Item.builder()
                .name("Item")
                .description("ItemDescription")
                .available(true)
                .owner(itemOwner).build();
    }

    Booking createBooking(Item item, User booker, BookingStatus status) {
        return Booking.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(4))
                .item(item)
                .booker(booker)
                .status(status)
                .build();
    }
}
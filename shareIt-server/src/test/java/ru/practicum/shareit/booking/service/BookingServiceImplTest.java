package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.entity.BookingState;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

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
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void createBookingTest() {
        User itemOwner = createUser(1L);
        User booker = createUser(2L);
        Item item = createItem(itemOwner);
        BookingDtoRequest dtoRequest = creatBookingDtoRequest(1L);
        Booking booking = createBooking(1L, item, booker, BookingStatus.WAITING);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));
        when(bookingRepository.save(any(Booking.class))).thenReturn(booking);

        BookingDtoResponse dtoResponse = bookingService.createBooking(dtoRequest, 2L);

        assertThat(dtoResponse).isNotNull();
        verify(bookingMapper).toBooking(any(BookingDtoRequest.class), any(Item.class), any(User.class));
        verify(itemRepository).findById(anyLong());
        verify(userRepository).findById(anyLong());
        verify(bookingRepository).save(any(Booking.class));
        verify(bookingMapper).toBookingDtoResponse(any(Booking.class));
    }

    @Test
    void createBooking_whenUserIsItemOwner_thenThrowException() {
        User itemOwner = createUser(1L);
        Item item = createItem(itemOwner);
        BookingDtoRequest dtoRequest = creatBookingDtoRequest(1L);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> bookingService.createBooking(dtoRequest, 1L))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage("There is no item with id=%s in the database", item.getId());
    }

    @Test
    void createBooking_whenItemIsNotAvailable_thenThrowException() {
        User itemOwner = createUser(1L);
        User booker = createUser(2L);
        Item item = createItem(itemOwner);
        item.setAvailable(false);
        BookingDtoRequest dtoRequest = creatBookingDtoRequest(5L);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        assertThatThrownBy(() -> bookingService.createBooking(dtoRequest, 2L))
                .isInstanceOf(ItemNotAvailableForBookingException.class)
                .hasMessage("Item with id=%s is not available for booking", item.getId());
    }

    @Test
    void createBooking_whenBookingDatesIsIncorrect_thenThrowException() {
        User itemOwner = createUser(1L);
        User booker = createUser(2L);
        Item item = createItem(itemOwner);
        BookingDtoRequest dtoRequest = creatBookingDtoRequest(5L);
        dtoRequest.setEnd(LocalDateTime.MIN);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(booker));

        assertThatThrownBy(() -> bookingService.createBooking(dtoRequest, 2L))
                .isInstanceOf(IncorrectBookingDatesException.class)
                .hasMessage("The end date of the booking %s cannot be earlier or equal" +
                        " the start date %s", dtoRequest.getEnd(), dtoRequest.getStart());
    }


    @Test
    void updateBooking_whenBookingWasApprovedByOwner_thenReturnApprovedStatus() {
        User itemOwner = createUser(1L);
        User booker = createUser(2L);
        Item item = createItem(itemOwner);
        Booking booking = createBooking(1L, item, booker, BookingStatus.WAITING);
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
        Booking booking = createBooking(1L, item, booker, BookingStatus.WAITING);
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
        Booking booking = createBooking(1L, item, booker, BookingStatus.WAITING);
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
        Booking booking = createBooking(1L, item, booker, BookingStatus.REJECTED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.updateBooking(3L, 7L, false))
                .isInstanceOf(NotPossibleChangeBookingStatusException.class)
                .hasMessage("The status for the booking with id=%s cannot be changed", booking.getId());
    }

    @Test
    void getBookingByIdTest() {
        User itemOwner = createUser(1L);
        User booker = createUser(2L);
        Item item = createItem(itemOwner);
        Booking booking = createBooking(99L, item, booker, BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        BookingDtoResponse dtoResponse = bookingService.getBookingById(99L, 2L);

        assertThat(dtoResponse).isNotNull();
        verify(bookingRepository).findById(anyLong());
        verify(bookingMapper).toBookingDtoResponse(any(Booking.class));
    }

    @Test
    void getBookingById_whenUserHasNoRights_thenThrowException() {
        User itemOwner = createUser(1L);
        User booker = createUser(2L);
        Item item = createItem(itemOwner);
        Long bookingId = 99L;
        Booking booking = createBooking(bookingId, item, booker, BookingStatus.APPROVED);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.getBookingById(bookingId, 50L))
                .isInstanceOf(BookingNotFoundException.class)
                .hasMessage("There is no booking with id=%s in the database", bookingId);
    }

    @Test
    void getAllByBookerId_whenStateIsUnknown_thenThrowException() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertThatThrownBy(() -> bookingService.getAllByBookerId(1L, BookingState.UNSUPPORTED_STATUS,
                0, 10))
                .isInstanceOf(UnknownStateException.class)
                .hasMessage("Unknown state: UNSUPPORTED_STATUS");
    }

    @Test
    void getAllByBookerId_whenUserDoesNotExist_thenThrowException() {
        Long userId = 99L;
        when(userRepository.existsById(userId)).thenReturn(false);

        assertThatThrownBy(() -> bookingService.getAllByBookerId(userId, BookingState.UNSUPPORTED_STATUS,
                0, 10))
                .isInstanceOf(UserNotFoundException.class)
                .hasMessage("There is no user with id=%s in the database", userId);
    }

    @Test
    void getAllByItemOwnerId_whenStateIsUnknown_thenThrowException() {
        when(userRepository.existsById(anyLong())).thenReturn(true);

        assertThatThrownBy(() -> bookingService.getAllByItemOwnerId(1L, BookingState.UNSUPPORTED_STATUS,
                0, 10))
                .isInstanceOf(UnknownStateException.class)
                .hasMessage("Unknown state: UNSUPPORTED_STATUS");
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

    Booking createBooking(Long id, Item item, User booker, BookingStatus status) {
        return Booking.builder()
                .id(id)
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(4))
                .item(item)
                .booker(booker)
                .status(status)
                .build();
    }

    private BookingDtoRequest creatBookingDtoRequest(Long itemId) {
        return BookingDtoRequest.builder()
                .itemId(itemId)
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusDays(1))
                .build();
    }
}
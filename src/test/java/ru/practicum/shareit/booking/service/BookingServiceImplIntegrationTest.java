package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.entity.State;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class BookingServiceImplIntegrationTest {
    private final BookingServiceImpl bookingService;
    private final BookingMapper bookingMapper;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Test
    void getAllByBookerIdTest() {
        User itemOwner = createUser("1");
        User booker = createUser("2");
        userRepository.saveAll(List.of(itemOwner, booker));
        Long bookerId = booker.getId();
        Item item1 = createItem("1", itemOwner);
        Item item2 = createItem("2", itemOwner);
        Item item3 = createItem("3", itemOwner);
        Item item4 = createItem("4", itemOwner);
        Item item5 = createItem("5", itemOwner);
        itemRepository.saveAll(List.of(item1, item2, item3, item4, item5));
        Booking pastBooking = createPastBooking(item1, booker);
        Booking currentBooking = createCurrentBooking(item2, booker);
        Booking futureBooking = createFutureBooking(item3, booker);
        Booking rejectedBooking = createRejectedBooking(item5, booker);
        bookingRepository.saveAll(List.of(pastBooking, currentBooking, futureBooking, rejectedBooking));

        List<BookingDtoResponse> all = bookingService.getAllByBookerId(bookerId, State.ALL, 0, 10);
        List<BookingDtoResponse> past = bookingService.getAllByBookerId(bookerId, State.PAST, 0, 10);
        List<BookingDtoResponse> current = bookingService.getAllByBookerId(bookerId, State.CURRENT, 0, 10);
        List<BookingDtoResponse> future = bookingService.getAllByBookerId(bookerId, State.FUTURE, 0, 10);
        List<BookingDtoResponse> waiting = bookingService.getAllByBookerId(bookerId, State.WAITING, 0, 10);
        List<BookingDtoResponse> rejected = bookingService.getAllByBookerId(bookerId, State.REJECTED, 0, 10);

        assertThat(all.size()).isEqualTo(4);
        assertThat(past.size()).isEqualTo(1);
        assertThat(past).contains(bookingMapper.toBookingDtoResponse(pastBooking));
        assertThat(current.size()).isEqualTo(2);
        assertThat(current).contains(bookingMapper.toBookingDtoResponse(currentBooking));
        assertThat(current).contains(bookingMapper.toBookingDtoResponse(rejectedBooking));
        assertThat(future.size()).isEqualTo(1);
        assertThat(future).contains(bookingMapper.toBookingDtoResponse(futureBooking));
        assertThat(waiting.size()).isEqualTo(1);
        assertThat(waiting).contains(bookingMapper.toBookingDtoResponse(futureBooking));
        assertThat(rejected.size()).isEqualTo(1);
        assertThat(rejected).contains(bookingMapper.toBookingDtoResponse(rejectedBooking));
    }

    @Test
    void getAllByItemOwnerIdTest() {
        User itemOwner = createUser("1");
        User booker = createUser("2");
        userRepository.saveAll(List.of(itemOwner, booker));
        Long ownerId = itemOwner.getId();
        Item item1 = createItem("1", itemOwner);
        Item item2 = createItem("2", itemOwner);
        Item item3 = createItem("3", itemOwner);
        Item item4 = createItem("4", itemOwner);
        Item item5 = createItem("5", itemOwner);
        itemRepository.saveAll(List.of(item1, item2, item3, item4, item5));
        Booking pastBooking = createPastBooking(item1, booker);
        Booking currentBooking = createCurrentBooking(item2, booker);
        Booking futureBooking = createFutureBooking(item3, booker);
        Booking rejectedBooking = createRejectedBooking(item5, booker);
        bookingRepository.saveAll(List.of(pastBooking, currentBooking, futureBooking, rejectedBooking));

        List<BookingDtoResponse> all = bookingService.getAllByItemOwnerId(ownerId, State.ALL, 0, 10);
        List<BookingDtoResponse> past = bookingService.getAllByItemOwnerId(ownerId, State.PAST, 0, 10);
        List<BookingDtoResponse> current = bookingService.getAllByItemOwnerId(ownerId, State.CURRENT, 0, 10);
        List<BookingDtoResponse> future = bookingService.getAllByItemOwnerId(ownerId, State.FUTURE, 0, 10);
        List<BookingDtoResponse> waiting = bookingService.getAllByItemOwnerId(ownerId, State.WAITING, 0, 10);
        List<BookingDtoResponse> rejected = bookingService.getAllByItemOwnerId(ownerId, State.REJECTED, 0, 10);

        assertThat(all.size()).isEqualTo(4);
        assertThat(past.size()).isEqualTo(1);
        assertThat(past).contains(bookingMapper.toBookingDtoResponse(pastBooking));
        assertThat(current.size()).isEqualTo(2);
        assertThat(current).contains(bookingMapper.toBookingDtoResponse(currentBooking));
        assertThat(current).contains(bookingMapper.toBookingDtoResponse(rejectedBooking));
        assertThat(future.size()).isEqualTo(1);
        assertThat(future).contains(bookingMapper.toBookingDtoResponse(futureBooking));
        assertThat(waiting.size()).isEqualTo(1);
        assertThat(waiting).contains(bookingMapper.toBookingDtoResponse(futureBooking));
        assertThat(rejected.size()).isEqualTo(1);
        assertThat(rejected).contains(bookingMapper.toBookingDtoResponse(rejectedBooking));
    }

    User createUser(String userPostfix) {
        return User.builder()
                .name(String.format("Name%s", userPostfix))
                .email(String.format("email@email%s.ru", userPostfix))
                .build();
    }

    Item createItem(String itemPostfix, User itemOwner) {
        return Item.builder()
                .name(String.format("Item%s", itemPostfix))
                .description(String.format("ItemDescription%s", itemPostfix))
                .available(true)
                .owner(itemOwner).build();
    }

    Booking createPastBooking(Item item, User booker) {
        return Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.CANCELED)
                .build();

    }

    private Booking createCurrentBooking(Item item, User booker) {
        return Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(1))
                .item(item)
                .booker(booker)
                .status(BookingStatus.APPROVED)
                .build();
    }

    Booking createFutureBooking(Item item, User booker) {
        return Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(item)
                .booker(booker)
                .status(BookingStatus.WAITING)
                .build();
    }

    Booking createRejectedBooking(Item item, User booker) {
        return Booking.builder()
                .start(LocalDateTime.now().minusDays(1))
                .end(LocalDateTime.now().plusDays(3))
                .item(item)
                .booker(booker)
                .status(BookingStatus.REJECTED)
                .build();
    }

}


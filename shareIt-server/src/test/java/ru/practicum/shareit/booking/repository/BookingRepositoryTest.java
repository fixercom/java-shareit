package ru.practicum.shareit.booking.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.util.DateUtils;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingRepositoryTest {
    private final BookingRepository bookingRepository;
    private final TestEntityManager entityManager;

    @Test
    void findAllPastUserBookings() {
        User booker = createUser("1");
        User itemOwner = createUser("2");
        entityManager.persist(booker);
        entityManager.persist(itemOwner);
        Item item1 = createItem("1", itemOwner);
        Item item2 = createItem("2", itemOwner);
        Item item3 = createItem("3", itemOwner);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
        Booking pastBooking1 = createPastBooking(item1, booker, BookingStatus.CANCELED);
        Booking currentBooking = createCurrentBooking(item2, booker, BookingStatus.APPROVED);
        Booking pastBooking2 = createPastBooking(item3, booker, BookingStatus.APPROVED);
        entityManager.persist(pastBooking1);
        entityManager.persist(currentBooking);
        entityManager.persist(pastBooking2);

        List<Booking> pastBookings = bookingRepository
                .findAllPastUserBookings(booker.getId(), DateUtils.now(), PageRequest.of(0, 10));

        assertThat(pastBookings.size()).isEqualTo(2);
        assertThat(pastBookings).containsExactly(pastBooking1, pastBooking2);
    }

    @Test
    void findAllCurrentUserBookings() {
        User booker = createUser("1");
        User itemOwner = createUser("2");
        entityManager.persist(booker);
        entityManager.persist(itemOwner);
        Item item1 = createItem("1", itemOwner);
        Item item2 = createItem("2", itemOwner);
        Item item3 = createItem("3", itemOwner);
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
        Booking pastBooking = createPastBooking(item1, booker, BookingStatus.CANCELED);
        Booking currentBooking1 = createCurrentBooking(item2, booker, BookingStatus.APPROVED);
        Booking currentBooking2 = createCurrentBooking(item3, booker, BookingStatus.REJECTED);
        entityManager.persist(pastBooking);
        entityManager.persist(currentBooking1);
        entityManager.persist(currentBooking2);

        List<Booking> pastBookings = bookingRepository
                .findAllCurrentUserBookings(booker.getId(), DateUtils.now(), PageRequest.of(0, 10));

        assertThat(pastBookings.size()).isEqualTo(2);
        assertThat(pastBookings).containsExactly(currentBooking1, currentBooking2);
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

    Booking createPastBooking(Item item, User booker, BookingStatus status) {
        return Booking.builder()
                .start(LocalDateTime.MIN)
                .end(DateUtils.now().minusSeconds(1))
                .item(item)
                .booker(booker)
                .status(status)
                .build();
    }

    Booking createCurrentBooking(Item item, User booker, BookingStatus status) {
        return Booking.builder()
                .start(DateUtils.now().minusSeconds(1))
                .end(DateUtils.now().plusHours(1))
                .item(item)
                .booker(booker)
                .status(status)
                .build();
    }
}
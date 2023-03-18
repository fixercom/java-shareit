package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.entity.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithDate;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.util.DateUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class ItemServiceImplIntegrationTest {
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private BookingRepository bookingRepository;

    @Test
    void getAllItemsByOwnerId() {
        User itemOwner = createUser("1");
        User anotherUser = createUser("2");
        User commentAuthor = createUser("3");
        userRepository.saveAll(List.of(itemOwner, anotherUser, commentAuthor));
        Long itemOwnerId = itemOwner.getId();
        Item item1 = createItem("1", itemOwner);
        Item item2 = createItem("2", anotherUser);
        Item item3 = createItem("3", itemOwner);
        itemRepository.saveAll(List.of(item1, item2, item3));
        Booking booking = Booking.builder()
                .start(DateUtils.now().minusDays(1))
                .end(DateUtils.now().plusDays(2))
                .item(item1)
                .booker(anotherUser)
                .status(BookingStatus.APPROVED)
                .build();
        bookingRepository.save(booking);
        Comment comment1ForItem3 = Comment.builder()
                .text("Comment1")
                .item(item3)
                .author(commentAuthor)
                .created(DateUtils.now())
                .build();
        Comment comment2ForItem3 = Comment.builder()
                .text("Comment2")
                .item(item3)
                .author(commentAuthor)
                .created(DateUtils.now())
                .build();
        commentRepository.saveAll(List.of(comment1ForItem3, comment2ForItem3));

        List<ItemDtoResponseWithDate> allItems = itemService.getAllItemsByOwnerId(itemOwnerId);

        assertThat(allItems.size()).isEqualTo(2);
        assertThat(allItems.get(0).getName()).isEqualTo("Item1");
        assertThat(allItems.get(0).getLastBooking()).isNotNull();
        assertThat(allItems.get(0).getNextBooking()).isNull();
        assertThat(allItems.get(1).getName()).isEqualTo("Item3");
        assertThat(allItems.get(1).getComments().size()).isEqualTo(2);
        assertThat(allItems.get(1).getComments().get(0).getText()).isEqualTo("Comment1");
        assertThat(allItems.get(1).getComments().get(1).getText()).isEqualTo("Comment2");
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
}
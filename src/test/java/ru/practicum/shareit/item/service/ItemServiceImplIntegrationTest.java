package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithDate;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@Transactional
class ItemServiceImplIntegrationTest {
    private final ItemService itemService;
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;

    @Test
    void getAllItemsByOwnerId() {
        User itemOwner = createUser("1");
        User anotherUser = createUser("2");
        userRepository.saveAll(List.of(itemOwner, anotherUser));
        Long itemOwnerId = itemOwner.getId();
        Item item1 = createItem("1", itemOwner);
        Item item2 = createItem("2", anotherUser);
        Item item3 = createItem("3", itemOwner);
        itemRepository.saveAll(List.of(item1, item2, item3));

        List<ItemDtoResponseWithDate> allItems = itemService.getAllItemsByOwnerId(itemOwnerId);

        assertThat(allItems.size()).isEqualTo(2);
        assertThat(allItems.get(0).getName()).isEqualTo("Item1");
        assertThat(allItems.get(1).getName()).isEqualTo("Item3");
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
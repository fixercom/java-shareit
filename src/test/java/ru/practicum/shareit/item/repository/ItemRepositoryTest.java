package ru.practicum.shareit.item.repository;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest()
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRepositoryTest {
    private final ItemRepository itemRepository;
    private final TestEntityManager entityManager;

    @Test
    void findAvailableItemsByText() {
        User owner = User.builder().name("Name").email("df@re.com").build();
        entityManager.persist(owner);
        Item item1 = Item.builder()
                .name("Hummer")
                .description("description1")
                .available(true)
                .owner(owner)
                .build();
        Item item2 = Item.builder()
                .name("Hummer2")
                .description("description2")
                .available(false)
                .owner(owner)
                .build();
        Item item3 = Item.builder()
                .name("Name")
                .description("huMMer")
                .available(true)
                .owner(owner)
                .build();
        Item item4 = Item.builder()
                .name("item")
                .description("something")
                .available(true)
                .owner(owner)
                .build();
        entityManager.persist(item1);
        entityManager.persist(item2);
        entityManager.persist(item3);
        entityManager.persist(item4);

        List<Item> items = itemRepository.findAvailableItemsByText("hummer");

        assertThat(items.size()).isEqualTo(2);
        assertThat(items).containsExactly(item1, item3);
    }
}
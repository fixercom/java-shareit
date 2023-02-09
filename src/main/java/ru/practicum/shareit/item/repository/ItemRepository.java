package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findAllByOwnerId(Long ownerId);

    @Query("select item from Item item" +
            " where upper(item.name) like upper(concat('%', ?1, '%'))" +
            "    or upper(item.description) like upper(concat('%', ?1, '%'))" +
            "   and item.available = true")
    List<Item> findAvailableItemsContainingInNameOrDescription(String text);
}

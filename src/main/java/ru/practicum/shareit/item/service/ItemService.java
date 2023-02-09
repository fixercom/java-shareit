package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;

import java.util.List;

public interface ItemService {
    Item createItem(ItemDto itemDto, Long ownerId);

    Item getItemById(Long id);

    List<Item> getAllItemsByOwnerId(Long id);

    Item updateItem(Long id, ItemDto itemDto, Long ownerId);

    List<Item> getAvailableItemsContainingInNameOrDescription(String text);
}

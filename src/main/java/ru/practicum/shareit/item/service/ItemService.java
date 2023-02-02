package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {
    Item createItem(Item item);

    Item getItemById(Long id);

    List<Item> getAllItemsByOwnerId(Long id);

    Item updateItem(Long id, Item item);

    List<Item> getItemsByText(String text);
}

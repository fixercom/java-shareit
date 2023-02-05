package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NotOwnerItemException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.storage.ItemStorage;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {

    private final ItemStorage itemStorage;

    @Override
    public Item createItem(Item item) {
        Item itemFromDatabase = itemStorage.save(item);
        log.debug("Item saved in the database with id={}: {}", itemFromDatabase.getId(), item);
        return itemFromDatabase;
    }

    @Override
    public Item getItemById(Long id) {
        Item itemFromDatabase = itemStorage.findById(id);
        if (itemFromDatabase == null) {
            throw new ItemNotFoundException(id);
        }
        log.debug("Item with id={} was obtained from the database: {}", id, itemFromDatabase);
        return itemFromDatabase;
    }

    @Override
    public List<Item> getAllItemsByOwnerId(Long id) {
        List<Item> allOwnerItems = itemStorage.findAllItemsByOwnerId(id);
        log.debug("All items for owner with id={} were obtained from the database: {}", id, allOwnerItems);
        return allOwnerItems;
    }

    @Override
    public Item updateItem(Long id, Item newItem) {
        Item oldItem = itemStorage.findById(id);
        Long newItemOwnerId = newItem.getOwner().getId();
        Long oldItemOwnerId = oldItem.getOwner().getId();
        if (!newItemOwnerId.equals(oldItemOwnerId)) {
            throw new NotOwnerItemException(id, newItemOwnerId);
        }
        Item itemWithUpdatedFields = patchFieldsForOldItemObject(oldItem, newItem);
        Item updatedItemFromDatabase = itemStorage.update(id, itemWithUpdatedFields);
        log.debug("Item with id={} successfully updated in the database: {}", id, updatedItemFromDatabase);
        return updatedItemFromDatabase;
    }

    @Override
    public List<Item> getItemsByText(String text) {
        List<Item> items = text.isEmpty() ? Collections.emptyList() : itemStorage.findItemsByText(text);
        log.debug("Items containing the text={} are received from the database: {}", text, items);
        return items;
    }

    private Item patchFieldsForOldItemObject(Item oldItem, Item newItem) {
        oldItem.setName(newItem.getName() == null ? oldItem.getName() : newItem.getName());
        oldItem.setDescription(newItem.getDescription() == null ? oldItem.getDescription() : newItem.getDescription());
        oldItem.setAvailable(newItem.getAvailable() == null ? oldItem.getAvailable() : newItem.getAvailable());
        return oldItem;
    }
}


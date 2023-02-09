package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NotOwnerItemException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    public Item createItem(ItemDto itemDto, Long ownerId) {
        User owner = userService.getUserById(ownerId);
        Item item = ItemMapper.toItem(itemDto, owner);
        Item itemFromDatabase = itemRepository.save(item);
        log.debug("Item saved in the database with id={}: {}", itemFromDatabase.getId(), item);
        return itemFromDatabase;
    }

    @Override
    public Item getItemById(Long id) {
        Item itemFromDatabase = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
        log.debug("Item with id={} was obtained from the database: {}", id, itemFromDatabase);
        return itemFromDatabase;
    }

    @Override
    public List<Item> getAllItemsByOwnerId(Long id) {
        List<Item> allOwnerItems = itemRepository.findAllByOwnerId(id);
        log.debug("All items for owner with id={} were obtained from the database: {}", id, allOwnerItems);
        return allOwnerItems;
    }

    @Override
    public Item updateItem(Long id, ItemDto itemDto, Long ownerId) {
        User owner = userService.getUserById(ownerId);
        Item newItem = ItemMapper.toItem(itemDto, owner);
        Item oldItem = getItemById(id);
        Long newItemOwnerId = newItem.getOwner().getId();
        Long oldItemOwnerId = oldItem.getOwner().getId();
        if (!newItemOwnerId.equals(oldItemOwnerId)) {
            throw new NotOwnerItemException(id, newItemOwnerId);
        }
        Item itemWithUpdatedFields = patchFieldsForOldItemObject(oldItem, newItem);
        Item updatedItemFromDatabase = itemRepository.save(itemWithUpdatedFields);
        log.debug("Item with id={} successfully updated in the database: {}", id, updatedItemFromDatabase);
        return updatedItemFromDatabase;
    }

    @Override
    public List<Item> getAvailableItemsContainingInNameOrDescription(String text) {
        List<Item> items = text.isEmpty() ? Collections.emptyList() :
                itemRepository.findAvailableItemsContainingInNameOrDescription(text);
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


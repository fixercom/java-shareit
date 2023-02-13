package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NotOwnerItemException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserService userService;
    private final ItemMapper itemMapper;

    @Override
    public Item createItem(ItemDto itemDto, Long ownerId) {
        User owner = userService.getUserById(ownerId);
        Item item = itemMapper.toItem(itemDto, owner);
        Item savedItem = itemRepository.save(item);
        log.debug("Item saved in the database with id={}: {}", savedItem.getId(), item);
        return savedItem;
    }

    @Override
    public Item getItemById(Long id) {
        Item item = itemRepository.findById(id).orElseThrow(() -> new ItemNotFoundException(id));
        log.debug("Item with id={} was obtained from the database: {}", id, item);
        return item;
    }

    @Override
    public List<Item> getAllItemsByOwnerId(Long id) {
        List<Item> allOwnerItems = itemRepository.findAllByOwnerId(id);
        log.debug("All items for owner with id={} were obtained from the database: {}", id, allOwnerItems);
        return allOwnerItems;
    }

    @Override
    public Item patchItem(Long id, ItemDto itemDto, Long ownerId) {
        Item oldItem = getItemById(id);
        Long oldItemOwnerId = oldItem.getOwner().getId();
        checkItemOwnerId(id, oldItemOwnerId, ownerId);
        Item oldItemWithPatch = itemMapper.patchItemFromDto(itemDto, oldItem);
        Item patchedItem = itemRepository.save(oldItemWithPatch);
        log.debug("Item with id={} successfully updated in the database: {}", id, patchedItem);
        return patchedItem;
    }

    @Override
    public List<Item> getAvailableItemsByText(String text) {
        List<Item> items = text.isEmpty() ? Collections.emptyList() :
                itemRepository.findAvailableItemsByText(text);
        log.debug("Items containing the text={} are received from the database: {}", text, items);
        return items;
    }

    private void checkItemOwnerId(Long itemId, Long oldOwnerId, Long newOwnerId) {
        if (!oldOwnerId.equals(newOwnerId)) {
            throw new NotOwnerItemException(itemId, newOwnerId);
        }
    }
}


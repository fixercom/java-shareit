package ru.practicum.shareit.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class ItemStorage extends Storage<Item> {
    @Override
    public Item save(Item item) {
        Long id = generateId();
        item.setId(id);
        return saveElement(id, item);
    }

    @Override
    public Item update(Long id, Item item) {
        item.setId(id);
        return saveElement(id, item);
    }

    public List<Item> findAllItemsByOwnerId(Long id) {
        return findAll().stream()
                .filter(item -> Objects.equals(item.getOwner().getId(), id))
                .collect(Collectors.toList());
    }

    public List<Item> findItemsByText(String text) {
        return findAll().stream()
                .filter(Item::getAvailable)
                .filter(item -> (item.getName().toLowerCase().contains(text.toLowerCase())) ||
                        (item.getDescription().toLowerCase().contains(text.toLowerCase())))
                .collect(Collectors.toList());
    }
}

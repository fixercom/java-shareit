package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.*;

import java.util.List;

public interface ItemService {
    ItemDtoResponse createItem(ItemDtoRequest itemDtoRequest, Long ownerId);

    ItemDtoResponseWithDate getItemById(Long id, Long userId);

    List<ItemDtoResponseWithDate> getAllItemsByOwnerId(Long id);

    ItemDtoResponse updateItem(Long id, ItemDtoRequest itemDtoRequest, Long ownerId);

    List<ItemDtoResponse> getAvailableItemsByText(String text);

    CommentDtoResponse createComment(Long itemId, CommentDtoRequest commentDtoRequest, Long userId);
}

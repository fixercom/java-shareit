package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

import java.util.List;

public interface ItemRequestService {
    ItemRequestDtoOut createItemRequest(ItemRequestDtoIn itemRequestDtoIn, Long userId);

    List<ItemRequestDtoOut> getAllOwnItemRequests(Long userId);

    List<ItemRequestDtoOut> getAllNotOwnItemRequests(Long userId, Integer from, Integer size);
}

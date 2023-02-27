package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;

public interface ItemRequestService {
    ItemRequestDtoOut createItemRequest(ItemRequestDtoIn itemRequestDtoIn, Long userId);
}

package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;

@Mapper
public interface ItemRequestMapper {
    ItemRequest toItemRequest(ItemRequestDtoIn itemRequestDtoIn, User requester, LocalDateTime created);

    ItemRequestDtoOut toItemRequestDtoOut(ItemRequest itemRequest);
}

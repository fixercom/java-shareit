package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ItemRequestMapper {
    ItemRequest toItemRequest(ItemRequestDtoIn itemRequestDtoIn, User requester, LocalDateTime created);

    @Mapping(target = "items", ignore = true)
    ItemRequestDtoOut toItemRequestDtoOut(ItemRequest itemRequest);

    @Mapping(target = "items", source = "itemDtoResponses")
    ItemRequestDtoOut toItemRequestDtoOut(ItemRequest itemRequest, List<ItemDtoResponse> itemDtoResponses);
}

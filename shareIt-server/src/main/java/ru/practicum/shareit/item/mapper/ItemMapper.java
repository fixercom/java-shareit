package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithDate;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

@Mapper
public interface ItemMapper {

    @Mapping(target = "id", source = "itemDtoRequest.id")
    @Mapping(target = "name", source = "itemDtoRequest.name")
    @Mapping(target = "description", source = "itemDtoRequest.description")
    Item toItem(ItemDtoRequest itemDtoRequest, User owner, ItemRequest request);

    @Mapping(target = "id", source = "item.id")
    ItemDtoResponseWithDate toItemDtoResponseWithDate(Item item, BookingDtoForItem lastBooking,
                                                      BookingDtoForItem nextBooking, List<CommentDtoResponse> comments);

    @Mapping(target = "requestId", source = "item.request.id")
    ItemDtoResponse toItemDtoResponse(Item item);

    List<ItemDtoResponse> toItemDtoResponseList(List<Item> items);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Item updateItemFromDto(ItemDtoRequest itemDtoRequest, @MappingTarget Item item);
}

package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.item.dto.CommentDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithDate;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

@Mapper
public interface ItemMapper {
    @Mapping(target = "id", source = "itemDtoRequest.id")
    @Mapping(target = "name", source = "itemDtoRequest.name")
    Item toItem(ItemDtoRequest itemDtoRequest, User owner);

    @Mapping(target = "id", source = "item.id")
    ItemDtoResponseWithDate toItemDtoResponseWithDate(Item item, BookingDtoForItem lastBooking,
                                                      BookingDtoForItem nextBooking, List<CommentDtoResponse> comments);

    ItemDtoResponse toItemDtoResponse(Item item);

    List<ItemDtoResponse> toItemDtoResponseList(List<Item> items);

    @Mapping(target = "owner", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Item updateItemFromDto(ItemDtoRequest itemDtoRequest, @MappingTarget Item item);
}

package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.user.entity.User;

import java.util.List;

@Mapper
public interface ItemMapper {
    @Mapping(target = "id", source = "itemDto.id")
    @Mapping(target = "name", source = "itemDto.name")
    Item toItem(ItemDto itemDto, User owner);

    ItemDto toItemDto(Item item);

    List<ItemDto> toItemDtoList(List<Item> items);

    @Mapping(target = "owner", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    Item patchItemFromDto(ItemDto itemDto, @MappingTarget Item item);
}

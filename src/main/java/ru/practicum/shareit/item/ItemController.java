package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.HeaderName;
import ru.practicum.shareit.validate.groups.OnCreate;
import ru.practicum.shareit.validate.groups.OnUpdate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    ItemDto createItem(@RequestHeader(HeaderName.ITEM_OWNER_ID) Long ownerId,
                       @RequestBody @Validated(OnCreate.class) ItemDto itemDto,
                       HttpServletRequest request) {
        log.debug("{} request {} received: {}", request.getMethod(), request.getRequestURI(), itemDto);
        return ItemMapper.toItemDto(itemService.createItem(itemDto, ownerId));
    }

    @GetMapping("/{id}")
    ItemDto getItemById(@PathVariable Long id, HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return ItemMapper.toItemDto(itemService.getItemById(id));
    }

    @GetMapping()
    List<ItemDto> getAllItemsByOwnerId(@RequestHeader(HeaderName.ITEM_OWNER_ID) Long ownerId,
                                       HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return ItemMapper.toItemDtoList(itemService.getAllItemsByOwnerId(ownerId));
    }

    @PatchMapping("/{id}")
    ItemDto updateItem(@RequestHeader(HeaderName.ITEM_OWNER_ID) Long ownerId,
                       @PathVariable Long id,
                       @RequestBody @Validated(OnUpdate.class) ItemDto itemDto,
                       HttpServletRequest request) {
        log.debug("{} request {} received: {}", request.getMethod(), request.getRequestURI(), itemDto);
        return ItemMapper.toItemDto(itemService.updateItem(id, itemDto, ownerId));
    }

    @GetMapping("/search")
    List<ItemDto> getAvailableItemsByText(@RequestParam("text") String text, HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return ItemMapper.toItemDtoList(itemService.getAvailableItemsContainingInNameOrDescription(text));
    }
}


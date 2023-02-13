package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.HeaderName;
import ru.practicum.shareit.validation.groups.OnCreate;
import ru.practicum.shareit.validation.groups.OnPatch;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;
    private final ItemMapper itemMapper;

    @PostMapping
    ItemDto createItem(@RequestHeader(HeaderName.ITEM_OWNER_ID) Long ownerId,
                       @RequestBody @Validated(OnCreate.class) ItemDto itemDto,
                       HttpServletRequest request) {
        log.debug("{} request {} received: {}", request.getMethod(), request.getRequestURI(), itemDto);
        return itemMapper.toItemDto(itemService.createItem(itemDto, ownerId));
    }

    @GetMapping("/{id}")
    ItemDto getItemById(@PathVariable Long id, HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return itemMapper.toItemDto(itemService.getItemById(id));
    }

    @GetMapping()
    List<ItemDto> getAllItemsByOwnerId(@RequestHeader(HeaderName.ITEM_OWNER_ID) Long ownerId,
                                       HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return itemMapper.toItemDtoList(itemService.getAllItemsByOwnerId(ownerId));
    }

    @PatchMapping("/{id}")
    ItemDto patchItem(@RequestHeader(HeaderName.ITEM_OWNER_ID) Long ownerId,
                      @PathVariable Long id,
                      @RequestBody @Validated(OnPatch.class) ItemDto itemDto,
                      HttpServletRequest request) {
        log.debug("{} request {} received: {}", request.getMethod(), request.getRequestURI(), itemDto);
        return itemMapper.toItemDto(itemService.patchItem(id, itemDto, ownerId));
    }

    @GetMapping("/search")
    List<ItemDto> getAvailableItemsByText(@RequestParam("text") String text, HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return itemMapper.toItemDtoList(itemService.getAvailableItemsByText(text));
    }
}


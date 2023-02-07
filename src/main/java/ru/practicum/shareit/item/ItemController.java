package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.validate.groups.OnCreate;
import ru.practicum.shareit.validate.groups.OnUpdate;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    ItemDto createItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
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
    List<ItemDto> getAllItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") Long ownerId, HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        List<Item> allOwnerItems = itemService.getAllItemsByOwnerId(ownerId);
        return allOwnerItems.stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @PatchMapping("/{id}")
    ItemDto updateItem(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                       @PathVariable Long id,
                       @RequestBody @Validated(OnUpdate.class) ItemDto itemDto,
                       HttpServletRequest request) {
        log.debug("{} request {} received: {}", request.getMethod(), request.getRequestURI(), itemDto);
        return ItemMapper.toItemDto(itemService.updateItem(id, itemDto, ownerId));
    }

    @GetMapping("/search")
    List<ItemDto> getItemsByText(@RequestParam("text") String text, HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return itemService.getItemsByText(text).stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}


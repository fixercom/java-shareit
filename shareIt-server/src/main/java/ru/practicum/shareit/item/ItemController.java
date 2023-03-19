package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.util.HeaderName;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDtoResponse createItem(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                      @RequestBody ItemDtoRequest itemDtoRequest,
                                      HttpServletRequest request) {
        log.debug("{} request {} received: {}", request.getMethod(), request.getRequestURI(), itemDtoRequest);
        return itemService.createItem(itemDtoRequest, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDtoResponse createComment(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                            @PathVariable Long itemId, HttpServletRequest request,
                                            @RequestBody CommentDtoRequest commentDtoRequest) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return itemService.createComment(itemId, commentDtoRequest, userId);
    }

    @GetMapping("/{id}")
    public ItemDtoResponseWithDate getItemById(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                               @PathVariable Long id,
                                               HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return itemService.getItemById(id, userId);
    }

    @GetMapping()
    public List<ItemDtoResponseWithDate> getAllItemsByOwnerId(@RequestHeader(HeaderName.SHARER_USER_ID) Long ownerId,
                                                              HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return itemService.getAllItemsByOwnerId(ownerId);
    }

    @PatchMapping("/{id}")
    public ItemDtoResponse updateItem(@RequestHeader(HeaderName.SHARER_USER_ID) Long ownerId,
                                      @PathVariable Long id,
                                      @RequestBody ItemDtoRequest itemDtoRequest,
                                      HttpServletRequest request) {
        log.debug("{} request {} received: {}", request.getMethod(), request.getRequestURI(), itemDtoRequest);
        return itemService.updateItem(id, itemDtoRequest, ownerId);
    }

    @GetMapping("/search")
    public List<ItemDtoResponse> getAvailableItemsByText(@RequestParam String text, HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return itemService.getAvailableItemsByText(text);
    }
}


package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.util.HeaderName;
import ru.practicum.shareit.validation.groups.OnCreate;
import ru.practicum.shareit.validation.groups.OnUpdate;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> createItem(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                             @RequestBody @Validated(OnCreate.class) ItemDtoRequest itemDtoRequest,
                                             HttpServletRequest request) {
        log.debug("{} request {} received: {}", request.getMethod(), request.getRequestURI(), itemDtoRequest);
        return itemClient.createItem(itemDtoRequest, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                                @PathVariable Long itemId, HttpServletRequest request,
                                                @RequestBody @Validated(OnCreate.class) CommentDtoRequest commentDtoRequest) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return itemClient.createComment(itemId, commentDtoRequest, userId);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                              @PathVariable Long id,
                                              HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return itemClient.getItemById(id, userId);
    }

    @GetMapping()
    public ResponseEntity<Object> getAllItemsByOwnerId(@RequestHeader(HeaderName.SHARER_USER_ID) Long ownerId,
                                                       HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return itemClient.getAllItemsByOwnerId(ownerId);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> updateItem(@RequestHeader(HeaderName.SHARER_USER_ID) Long ownerId,
                                             @PathVariable Long id,
                                             @RequestBody @Validated(OnUpdate.class) ItemDtoRequest itemDtoRequest,
                                             HttpServletRequest request) {
        log.debug("{} request {} received: {}", request.getMethod(), request.getRequestURI(), itemDtoRequest);
        return itemClient.updateItem(id, itemDtoRequest, ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAvailableItemsByText(@RequestParam String text, HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return itemClient.getAvailableItemsByText(text);
    }
}

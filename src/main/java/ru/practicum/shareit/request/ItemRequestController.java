package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.HeaderName;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    ItemRequestDtoOut createItemRequest(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                        @RequestBody @Valid ItemRequestDtoIn itemRequestDtoIn,
                                        HttpServletRequest request) {
        log.debug("{} request {} received: {}", request.getMethod(), request.getRequestURI(), itemRequestDtoIn);
        return itemRequestService.createItemRequest(itemRequestDtoIn, userId);
    }

    @GetMapping
    List<ItemRequestDtoOut> getAllOwnItemRequests(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                                  HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return itemRequestService.getAllOwnItemRequests(userId);
    }

    @GetMapping("/all")
    List<ItemRequestDtoOut> getAllNotOwnItemRequests(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                                     @RequestParam(defaultValue = "0") @Min(0) Integer from,
                                                     @RequestParam(defaultValue = "100") @Min(1) Integer size,
                                                     HttpServletRequest request) {
        log.debug("{} request {}?{} received", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemRequestService.getAllNotOwnItemRequests(userId, from, size);
    }

    @GetMapping("/{id}")
    ItemRequestDtoOut getItemRequestById(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                         @PathVariable Long id,
                                         HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return itemRequestService.getItemRequestById(id, userId);
    }
}

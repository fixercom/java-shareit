package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.util.HeaderName;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
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
                                                     @RequestParam @Min(0) Optional<Integer> from,
                                                     @RequestParam @Min(1) Optional<Integer> size,
                                                     HttpServletRequest request) {
        log.debug("{} request {}?{} received", request.getMethod(), request.getRequestURI(), request.getQueryString());
        return itemRequestService.getAllNotOwnItemRequests(userId, from.orElse(0), size.orElse(100));
    }

    @GetMapping("/{id}")
    ItemRequestDtoOut getItemRequestById(@RequestHeader(HeaderName.SHARER_USER_ID) Long userId,
                                         @PathVariable Long id,
                                         HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        return itemRequestService.getItemRequestById(id, userId);
    }
}

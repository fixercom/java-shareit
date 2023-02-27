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
}

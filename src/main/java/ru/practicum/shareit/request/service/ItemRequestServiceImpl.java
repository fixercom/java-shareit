package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final UserService userService;

    @Override
    @Transactional
    public ItemRequestDtoOut createItemRequest(ItemRequestDtoIn itemRequestDtoIn, Long userId) {
        User requester = userService.getUserEntityById(userId);
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDtoIn, requester, LocalDateTime.now());
        itemRequest.setRequester(requester);
        ItemRequestDtoOut dtoOut = itemRequestMapper.toItemRequestDtoOut(itemRequestRepository.save(itemRequest));
        log.debug("Item request saved in the database with id={}: {}", dtoOut.getId(), dtoOut);
        return dtoOut;
    }
}

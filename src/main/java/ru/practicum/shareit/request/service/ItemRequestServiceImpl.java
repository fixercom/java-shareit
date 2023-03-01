package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class ItemRequestServiceImpl implements ItemRequestService {
    private final UserService userService;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemRepository itemRepository;
    private final ItemMapper itemMapper;

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

    @Override
    public List<ItemRequestDtoOut> getAllOwnItemRequests(Long userId) {
        userService.checkUserExists(userId);
        Sort sort = Sort.by("created").descending();
        List<ItemRequest> itemRequests = itemRequestRepository.findAllByRequesterId(userId, sort);
        List<Item> items = itemRepository.findAllByRequestIn(itemRequests);
        List<ItemRequestDtoOut> itemRequestDtoOuts = generateItemRequestDtoOutList(itemRequests, items);
        log.debug("Received a list of item requests for the user with id={}", userId);
        return itemRequestDtoOuts;
    }

    @Override
    public List<ItemRequestDtoOut> getAllNotOwnItemRequests(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size, Sort.by("created").descending());
        List<ItemRequest> itemRequests = itemRequestRepository.findAllNotOwnItemRequests(userId, pageable);
        List<Item> items = itemRepository.findAllByRequestIn(itemRequests);
        List<ItemRequestDtoOut> itemRequestDtoOuts = generateItemRequestDtoOutList(itemRequests, items);
        log.debug("Received a list of item requests");
        return itemRequestDtoOuts;
    }

    private List<ItemRequestDtoOut> generateItemRequestDtoOutList(List<ItemRequest> requests, List<Item> items) {
        Map<ItemRequest, List<ItemDtoResponse>> itemReqestMap = groupItemsByItemRequestKey(items);
        List<ItemRequestDtoOut> resultList = new ArrayList<>();
        for (ItemRequest request : requests) {
            List<ItemDtoResponse> itemDtoResponses = itemReqestMap.getOrDefault(request, Collections.emptyList());
            resultList.add(itemRequestMapper.toItemRequestDtoOut(request, itemDtoResponses));
        }
        return resultList;
    }

    private Map<ItemRequest, List<ItemDtoResponse>> groupItemsByItemRequestKey(List<Item> items) {
        Map<ItemRequest, List<ItemDtoResponse>> itemRequestsMap = new HashMap<>();
        for (Item item : items) {
            ItemRequest itemRequest = item.getRequest();
            List<ItemDtoResponse> itemsForItemRequest = itemRequestsMap.getOrDefault(itemRequest, new ArrayList<>());
            itemsForItemRequest.add(itemMapper.toItemDtoResponse(item));
            itemRequestsMap.put(itemRequest, itemsForItemRequest);
        }
        return itemRequestsMap;
    }
}

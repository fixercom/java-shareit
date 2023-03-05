package ru.practicum.shareit.request.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private UserService userService;
    @Spy
    private ItemRequestMapper itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);
    @InjectMocks
    private ItemRequestServiceImpl itemRequestService;

    @Test
    void createItemRequestTest() {
        ItemRequestDtoIn dtoIn = ItemRequestDtoIn.builder()
                .description("Test description")
                .build();
        User requester = User.builder()
                .id(1L)
                .name("Name")
                .email("email@email.ru")
                .build();
        ItemRequest itemRequest = ItemRequest.builder()
                .id(1L)
                .description("Test description")
                .requester(requester)
                .created(LocalDateTime.MIN)
                .build();
        Mockito.when(userService.getUserEntityById(anyLong())).thenReturn(requester);
        Mockito.when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        itemRequestService.createItemRequest(dtoIn, requester.getId());

        InOrder inOrder = Mockito.inOrder(userService, itemRequestMapper, itemRequestRepository);
        inOrder.verify(userService).getUserEntityById(anyLong());
        inOrder.verify(itemRequestMapper)
                .toItemRequest(any(ItemRequestDtoIn.class), any(User.class), any(LocalDateTime.class));
        inOrder.verify(itemRequestRepository).save(any(ItemRequest.class));
        inOrder.verify(itemRequestMapper).toItemRequestDtoOut(any(ItemRequest.class));
    }
}
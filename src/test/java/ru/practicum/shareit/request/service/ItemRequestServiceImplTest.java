package ru.practicum.shareit.request.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.exception.ItemRequestNotFoundException;
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
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemRequestServiceImplTest {
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    @Spy
    private ItemRequestMapper itemRequestMapper = Mappers.getMapper(ItemRequestMapper.class);
    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
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
        when(userService.getUserEntityById(anyLong())).thenReturn(requester);
        when(itemRequestRepository.save(any(ItemRequest.class))).thenReturn(itemRequest);

        itemRequestService.createItemRequest(dtoIn, requester.getId());

        InOrder inOrder = Mockito.inOrder(userService, itemRequestMapper, itemRequestRepository);
        inOrder.verify(userService).getUserEntityById(anyLong());
        inOrder.verify(itemRequestMapper)
                .toItemRequest(any(ItemRequestDtoIn.class), any(User.class), any(LocalDateTime.class));
        inOrder.verify(itemRequestRepository).save(any(ItemRequest.class));
        inOrder.verify(itemRequestMapper).toItemRequestDtoOut(any(ItemRequest.class));
    }

    @Test
    void getAllNotOwnItemRequestsTest() {
        Long userId = 7L;
        Long requesterId = 8L;
        User requester = User.builder().id(requesterId).build();
        ItemRequest itemRequest1 = ItemRequest.builder().id(1L).requester(requester).build();
        ItemRequest itemRequest2 = ItemRequest.builder().id(2L).requester(requester).build();
        Item item1 = Item.builder().id(44L).request(itemRequest1).build();
        Item item2 = Item.builder().id(55L).request(itemRequest2).build();
        when(itemRequestRepository.findAllNotOwnItemRequests(anyLong(), any(Pageable.class)))
                .thenReturn(List.of(itemRequest1, itemRequest2));
        when(itemRepository.findAllByRequestIn(anyList())).thenReturn(List.of(item1, item2));

        List<ItemRequestDtoOut> dtoOuts = itemRequestService.getAllNotOwnItemRequests(userId, 0, 10);

        assertThat(dtoOuts).size().isEqualTo(2);
        assertThat(dtoOuts.get(0).getItems().get(0).getRequestId()).isEqualTo(1L);
        assertThat(dtoOuts.get(0).getItems().get(0).getId()).isEqualTo(44L);
        assertThat(dtoOuts.get(1).getItems().get(0).getRequestId()).isEqualTo(2L);
        assertThat(dtoOuts.get(1).getItems().get(0).getId()).isEqualTo(55L);
        verify(itemRequestRepository).findAllNotOwnItemRequests(anyLong(), any(Pageable.class));
        verify(itemRepository).findAllByRequestIn(anyList());
        verify(itemMapper, times(2)).toItemDtoResponse(any(Item.class));
        verify(itemRequestMapper, times(2))
                .toItemRequestDtoOut(any(ItemRequest.class), anyList());
    }

    @Test
    void getItemRequestById_whenItemRequestIsPresent_thenReturnItemRequestDtoOut() {
        Long itemRequestId = 1L;
        ItemRequest itemRequest = ItemRequest.builder().id(itemRequestId).description("IR1").build();
        Item item1 = Item.builder().id(10L).request(itemRequest).build();
        Item item2 = Item.builder().id(11L).request(itemRequest).build();
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequest(any(ItemRequest.class))).thenReturn(List.of(item1, item2));

        ItemRequestDtoOut dtoOut = itemRequestService.getItemRequestById(itemRequestId, 1L);

        assertThat(dtoOut.getItems().size()).isEqualTo(2);
        assertThat(dtoOut.getItems().get(0).getId()).isEqualTo(10);
        assertThat(dtoOut.getItems().get(1).getId()).isEqualTo(11);
        verify(itemRequestRepository).findById(anyLong());
        verify(itemMapper).toItemDtoResponseList(anyList());
        verify(itemRequestMapper).toItemRequestDtoOut(any(ItemRequest.class), anyList());
    }

    @Test
    void getItemRequestById_whenItemRequestIsAbsent_thenThrowException() {
        Long itemRequestId = 1L;
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> itemRequestService.getItemRequestById(itemRequestId, 1L))
                .isInstanceOf(ItemRequestNotFoundException.class)
                .hasMessage("There is no item request with id=%s in the database", itemRequestId);
    }
}
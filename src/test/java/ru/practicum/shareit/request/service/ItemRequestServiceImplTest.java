package ru.practicum.shareit.request.service;


import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.request.dto.ItemRequestDtoIn;
import ru.practicum.shareit.request.dto.ItemRequestDtoOut;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
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
    void testCreateItemRequest() {
        User user = createUser();
        Mockito.when(userService.getUserEntityById(anyLong()))
                .thenReturn(user);
        Mockito.when(itemRequestRepository.save(any(ItemRequest.class)))
                .thenReturn(createItemRequest(user));
        ItemRequestDtoIn dtoIn = createItemRequestDtoIn();
        ItemRequestDtoOut dtoOut = itemRequestService.createItemRequest(dtoIn, 1L);
        assertThat(dtoOut).isEqualTo(createItemRequestDtoOut());
    }

    User createUser() {
        return User.builder()
                .id(1L)
                .name("testName")
                .email("email@gt.ru")
                .build();
    }

    ItemRequestDtoIn createItemRequestDtoIn() {
        return ItemRequestDtoIn.builder()
                .description("Test description")
                .build();
    }

    ItemRequest createItemRequest(User requester) {
        return ItemRequest.builder()
                .id(1L)
                .description("Test description")
                .requester(requester)
                .created(LocalDateTime.MIN)
                .build();
    }

    ItemRequestDtoOut createItemRequestDtoOut() {
        return ItemRequestDtoOut.builder()
                .id(1L)
                .description("Test description")
                .created(LocalDateTime.MIN)
                .build();
    }
}
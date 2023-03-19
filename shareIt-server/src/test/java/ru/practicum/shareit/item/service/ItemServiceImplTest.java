package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.entity.Booking;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NotOwnerItemException;
import ru.practicum.shareit.exception.UserDidNotBookingItemException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.entity.Comment;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.entity.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.entity.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void createItem_whenItemRequestIsNull_thenReturnItemDtoResponseWithoutRequestId() {
        Long ownerId = 12L;
        User owner = User.builder()
                .id(ownerId)
                .name("Name")
                .email("rew@ty.com")
                .build();
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name("Saw")
                .description("Sharp saw")
                .available(true)
                .build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRepository.save(any(Item.class))).then(AdditionalAnswers.returnsFirstArg());

        ItemDtoResponse dtoResponse = itemService.createItem(itemDtoRequest, ownerId);

        assertThat(dtoResponse.getName()).isEqualTo("Saw");
        assertThat(dtoResponse.getDescription()).isEqualTo("Sharp saw");
        assertThat(dtoResponse.getAvailable()).isTrue();
        verify(userRepository).findById(anyLong());
        verify(itemMapper).toItem(any(ItemDtoRequest.class), any(User.class), any());
        verify(itemRepository).save(any(Item.class));
        verify(itemMapper).toItemDtoResponse(any(Item.class));
    }

    @Test
    void createItem_whenItemRequestIsNotNull_thenReturnItemDtoResponseWithRequestId() {
        Long ownerId = 12L;
        Long requestId = 3L;
        User owner = User.builder()
                .id(ownerId)
                .name("Name")
                .email("rew@ty.com")
                .build();
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name("Saw")
                .description("Sharp saw")
                .available(true)
                .requestId(requestId)
                .build();
        ItemRequest itemRequest = ItemRequest.builder().id(requestId).build();
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(owner));
        when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        when(itemRepository.save(any(Item.class))).then(AdditionalAnswers.returnsFirstArg());

        ItemDtoResponse dtoResponse = itemService.createItem(itemDtoRequest, ownerId);

        assertThat(dtoResponse.getName()).isEqualTo("Saw");
        assertThat(dtoResponse.getDescription()).isEqualTo("Sharp saw");
        assertThat(dtoResponse.getAvailable()).isTrue();
        assertThat(dtoResponse.getRequestId()).isEqualTo(requestId);
        verify(userRepository).findById(anyLong());
        verify(itemRequestRepository).findById(anyLong());
        verify(itemMapper).toItem(any(ItemDtoRequest.class), any(User.class), any());
        verify(itemRepository).save(any(Item.class));
        verify(itemMapper).toItemDtoResponse(any(Item.class));
    }

    @Test
    void updateItem_whenSuccessful_thenReturnItemDtoResponse() {
        User itemOwner = User.builder()
                .id(5L)
                .name("Owner name")
                .email("owner@rt.ru")
                .build();
        Item item = Item.builder()
                .id(22L)
                .name("Item name")
                .description("Item description")
                .available(true)
                .owner(itemOwner)
                .build();
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name("Updated name")
                .available(false)
                .build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(itemRepository.save(any(Item.class))).then(AdditionalAnswers.returnsFirstArg());

        ItemDtoResponse dtoResponse = itemService.updateItem(22L, itemDtoRequest, 5L);

        assertThat(dtoResponse.getId()).isEqualTo(22);
        assertThat(dtoResponse.getName()).isEqualTo("Updated name");
        assertThat(dtoResponse.getDescription()).isEqualTo("Item description");
        assertThat(dtoResponse.getAvailable()).isFalse();
        verify(itemRepository).findById(anyLong());
        verify(itemMapper).updateItemFromDto(any(ItemDtoRequest.class), any(Item.class));
        verify(itemRepository).save(any(Item.class));
        verify(itemMapper).toItemDtoResponse(any(Item.class));
    }

    @Test
    void updateItem_whenNotItemOwner_thenThrowException() {
        Long userId = 7L;
        Long itemOwnerId = 5L;
        Long itemId = 22L;
        User itemOwner = User.builder()
                .id(itemOwnerId)
                .name("Owner name")
                .email("owner@rt.ru")
                .build();
        Item item = Item.builder()
                .id(itemId)
                .name("Item name")
                .description("Item description")
                .available(true)
                .owner(itemOwner)
                .build();
        ItemDtoRequest itemDtoRequest = ItemDtoRequest.builder()
                .name("Updated name")
                .available(false)
                .build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThatThrownBy(() -> itemService.updateItem(itemId, itemDtoRequest, userId))
                .isInstanceOf(NotOwnerItemException.class)
                .hasMessage("User with id=%s is not the owner of the item with id=%s", userId, itemId);
    }

    @Test
    void getItemById_whenItemIsAbsent_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> itemService.getItemById(33L, 1L))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage("There is no item with id=%s in the database", 33L);
    }

    @Test
    void getItemById_whenItemIsPresentAndItemOwnerMakeRequest() {
        User user = User.builder()
                .id(2L)
                .name("Name")
                .email("sd@gt.ru")
                .build();
        Item item = Item.builder()
                .id(5L)
                .name("Item")
                .description("description")
                .available(true)
                .owner(user)
                .build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllSortedByStartApprovedBookingsByItemId(anyLong()))
                .thenReturn(Collections.emptyList());
        when((commentRepository.findAllByItemId(anyLong()))).thenReturn(Collections.emptyList());

        ItemDtoResponseWithDate dto = itemService.getItemById(5L, 2L);

        assertThat(dto.getId()).isEqualTo(5);
        assertThat(dto.getName()).isEqualTo("Item");
        assertThat(dto.getDescription()).isEqualTo("description");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getLastBooking()).isNull();
        assertThat(dto.getNextBooking()).isNull();
        assertThat(dto.getComments()).isEmpty();

        verify(itemRepository).findById(anyLong());
        verify(bookingRepository).findAllSortedByStartApprovedBookingsByItemId(anyLong());
        verify(commentRepository).findAllByItemId(anyLong());
        verify(bookingMapper, times(2)).toBookingDtoForItem(any());
        verify(commentMapper).toCommentDtoResponseList(anyList());
        verify(itemMapper).toItemDtoResponseWithDate(any(Item.class), any(), any(), anyList());
    }

    @Test
    void getItemById_whenItemIsPresentAndNotItemOwnerMakeRequest() {
        User user = User.builder()
                .id(2L)
                .name("Name")
                .email("sd@gt.ru")
                .build();
        Item item = Item.builder()
                .id(5L)
                .name("Item")
                .description("description")
                .available(true)
                .owner(user)
                .build();
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(bookingRepository.findAllSortedByStartApprovedBookingsByItemId(anyLong()))
                .thenReturn(Collections.emptyList());
        when((commentRepository.findAllByItemId(anyLong()))).thenReturn(Collections.emptyList());

        ItemDtoResponseWithDate dto = itemService.getItemById(5L, 3L);

        assertThat(dto.getId()).isEqualTo(5);
        assertThat(dto.getName()).isEqualTo("Item");
        assertThat(dto.getDescription()).isEqualTo("description");
        assertThat(dto.getAvailable()).isTrue();
        assertThat(dto.getLastBooking()).isNull();
        assertThat(dto.getNextBooking()).isNull();
        assertThat(dto.getComments()).isEmpty();

        verify(itemRepository).findById(anyLong());
        verify(bookingRepository).findAllSortedByStartApprovedBookingsByItemId(anyLong());
        verify(commentRepository).findAllByItemId(anyLong());
        verify(commentMapper).toCommentDtoResponseList(anyList());
        verify(itemMapper).toItemDtoResponseWithDate(any(Item.class), any(), any(), anyList());
    }

    @Test
    void getAvailableItemsByText_whenTextParamIsNotBlank() {
        List<Item> items = List.of(
                Item.builder().build(),
                Item.builder().build()
        );
        when(itemRepository.findAvailableItemsByText(anyString())).thenReturn(items);
        List<ItemDtoResponse> resultList = itemService.getAvailableItemsByText("Any text");

        assertThat(resultList.size()).isEqualTo(2);
        verify(itemRepository).findAvailableItemsByText(anyString());
        verify(itemMapper).toItemDtoResponseList(anyList());
    }

    @Test
    void getAvailableItemsByText_whenTextParamIsBlank() {
        List<ItemDtoResponse> resultList = itemService.getAvailableItemsByText("");

        assertThat(resultList).isEmpty();
    }

    @Test
    void createComment_whenSuccessful_thenReturnCommentDtoResponse() {
        Long itemId = 14L;
        Long userId = 3L;
        Item item = Item.builder().id(itemId).build();
        User author = User.builder().id(userId).name("Jonn").build();
        CommentDtoRequest dtoRequest = CommentDtoRequest.builder().text("Text for comment").build();
        when(bookingRepository.findAllRealItemBookingsForUserAtTheMoment(
                anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(List.of(Booking.builder().build()));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        when(userRepository.findById(anyLong())).thenReturn(Optional.of(author));
        when(commentRepository.save(any(Comment.class))).then(AdditionalAnswers.returnsFirstArg());

        CommentDtoResponse dtoResponse = itemService.createComment(itemId, dtoRequest, userId);

        assertThat(dtoResponse.getText()).isEqualTo("Text for comment");
        assertThat(dtoResponse.getAuthorName()).isEqualTo("Jonn");
        verify(bookingRepository).findAllRealItemBookingsForUserAtTheMoment(
                anyLong(), anyLong(), any(LocalDateTime.class));
        verify(itemRepository).findById(anyLong());
        verify(userRepository).findById(anyLong());
        verify(commentMapper).toComment(any(CommentDtoRequest.class), any(Item.class),
                any(User.class),any(LocalDateTime.class));
        verify(commentRepository).save(any(Comment.class));
        verify(commentMapper).toCommentDtoResponse(any(Comment.class));
    }

    @Test
    void createComment_whenUserHasNoBookedItemInThePast_thenThrowException() {
        Long itemId = 14L;
        Long userId = 3L;
        CommentDtoRequest commentDtoRequest = CommentDtoRequest.builder().build();
        when(bookingRepository.findAllRealItemBookingsForUserAtTheMoment(
                anyLong(), anyLong(), any(LocalDateTime.class))).thenReturn(Collections.emptyList());

        assertThatThrownBy(() -> itemService.createComment(itemId, commentDtoRequest, userId))
                .isInstanceOf(UserDidNotBookingItemException.class)
                .hasMessage("The user with id=%s did not book the item with id=%s", userId, itemId);
    }
}
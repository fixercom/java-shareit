package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.item.dto.ItemDtoResponseWithDate;
import ru.practicum.shareit.item.entity.Item;
import ru.practicum.shareit.item.mapper.CommentMapper;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.entity.User;

import java.util.Collections;
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
    @Spy
    private BookingMapper bookingMapper = Mappers.getMapper(BookingMapper.class);
    @Spy
    private ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    @Spy
    private CommentMapper commentMapper = Mappers.getMapper(CommentMapper.class);
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void getItemById_whenItemIsAbsent_thenThrowException() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThatThrownBy(() -> itemService.getItemById(33L, 1L))
                .isInstanceOf(ItemNotFoundException.class)
                .hasMessage("There is no item with id=%s in the database", 33L);
    }

    @Test
    void getItemById_whenItemIsPresent() {
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

}
package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;

import java.util.List;

@Data
@Builder
public class ItemDtoResponseWithDate {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private BookingDtoForItem lastBooking;
    private BookingDtoForItem nextBooking;
    private List<CommentDtoResponse> comments;
}

package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CommentDtoResponse {
    private Long id;
    private String text;
    private String authorName;
}

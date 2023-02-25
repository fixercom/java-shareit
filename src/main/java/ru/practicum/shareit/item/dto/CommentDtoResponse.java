package ru.practicum.shareit.item.dto;

import lombok.Data;

@Data
public class CommentDtoResponse {
    private Long id;
    private String text;
    private String authorName;
}

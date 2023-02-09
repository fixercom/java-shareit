package ru.practicum.shareit.request.entity;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.entity.User;

import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequest {
    private Long id;
    private String description;
    private User requester;
    private LocalDateTime created;
}

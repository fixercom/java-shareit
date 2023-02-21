package ru.practicum.shareit.item.dto;

import lombok.Data;
import ru.practicum.shareit.validation.groups.OnCreate;

import javax.validation.constraints.NotBlank;

@Data
public class CommentDtoRequest {
    @NotBlank(groups = OnCreate.class, message = "text must not be empty or null")
    private String text;
}

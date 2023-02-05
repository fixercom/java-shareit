package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(message = "name should not be empty or null")
    private String name;
    @NotBlank(message = "description should not be empty or null")
    private String description;
    @NotNull(message = "available should not be null")
    private Boolean available;
    private ItemRequest request;
}

package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.annotation.NotBlankButMayBeNull;
import ru.practicum.shareit.validation.groups.OnCreate;
import ru.practicum.shareit.validation.groups.OnUpdate;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;

@Data
@Builder
public class ItemDtoRequest {
    @Null(groups = OnCreate.class, message = "id must be null")
    private Long id;
    @NotBlank(groups = OnCreate.class, message = "name must not be empty or null")
    @NotBlankButMayBeNull(groups = OnUpdate.class, message = "name must not be empty")
    private String name;
    @NotBlank(groups = OnCreate.class, message = "description must not be empty or null")
    @NotBlankButMayBeNull(groups = OnUpdate.class, message = "description must not be empty")
    private String description;
    @NotNull(groups = OnCreate.class, message = "available must not be null")
    private Boolean available;
    private Long requestId;
}

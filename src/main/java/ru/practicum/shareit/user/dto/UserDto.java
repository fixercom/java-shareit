package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validate.annotation.NotBlankButMayBeNull;
import ru.practicum.shareit.validate.groups.OnCreate;
import ru.practicum.shareit.validate.groups.OnUpdate;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
@Builder
public class UserDto {
    @Null(groups = OnCreate.class, message = "id must be null")
    private Long id;
    @NotBlank(groups = OnCreate.class, message = "name must not be empty or null")
    @NotBlankButMayBeNull(groups = OnUpdate.class, message = "name must not be empty")
    private String name;
    @Email(groups = {OnCreate.class, OnUpdate.class}, message = "incorrect email address")
    @NotBlank(groups = OnCreate.class, message = "email address must not be empty or null")
    @NotBlankButMayBeNull(groups = OnUpdate.class, message = "email address must not be empty")
    private String email;
}

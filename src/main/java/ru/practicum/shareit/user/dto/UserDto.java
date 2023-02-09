package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.validation.annotation.NotBlankButMayBeNull;
import ru.practicum.shareit.validation.groups.OnCreate;
import ru.practicum.shareit.validation.groups.OnPatch;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Null;

@Data
@Builder
public class UserDto {
    @Null(groups = OnCreate.class, message = "id must be null")
    private Long id;
    @NotBlank(groups = OnCreate.class, message = "name must not be empty or null")
    @NotBlankButMayBeNull(groups = OnPatch.class, message = "name must not be empty")
    private String name;
    @Email(groups = {OnCreate.class, OnPatch.class}, message = "incorrect email address")
    @NotBlank(groups = OnCreate.class, message = "email address must not be empty or null")
    @NotBlankButMayBeNull(groups = OnPatch.class, message = "email address must not be empty")
    private String email;
}

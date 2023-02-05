package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {
    private Long id;
    private String name;
    @Email(message = "incorrect email address")
    @NotBlank(message = "email address should not be empty or null")
    private String email;
}

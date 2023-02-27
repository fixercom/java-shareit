package ru.practicum.shareit.request.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemRequestDtoIn {
    @NotBlank(message = "Description must not be empty or null")
    private String description;
}

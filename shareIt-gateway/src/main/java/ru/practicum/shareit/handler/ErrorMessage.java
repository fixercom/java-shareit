package ru.practicum.shareit.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorMessage {
    private final Integer statusCode;
    private final String error;
}

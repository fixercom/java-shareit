package ru.practicum.shareit.exception.handler;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ErrorMessage {
    private final Integer statusCode;
    private final String message;
}

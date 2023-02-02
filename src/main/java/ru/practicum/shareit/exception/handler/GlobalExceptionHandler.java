package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.EmailIsAlreadyInUseException;
import ru.practicum.shareit.exception.ItemNotFoundException;
import ru.practicum.shareit.exception.NotOwnerItemException;
import ru.practicum.shareit.exception.UserNotFoundException;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    private static final String YELLOW_COLOR_LOG = "\033[33m";
    private static final String ORIGINAL_COLOR_LOG = "\033[0m";

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMethodArgumentNotValidException(MethodArgumentNotValidException exception,
                                                              HttpServletRequest request) {
        log.debug("{} request {} received: {}", request.getMethod(), request.getRequestURI(), exception.getTarget());
        String fieldName = Objects.requireNonNull(exception.getFieldError()).getField();
        Object rejectedValue = Objects.requireNonNull(exception.getFieldError()).getRejectedValue();
        String message = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        log.warn("{}MethodArgumentNotValidException: {}Field {}=\"{}\" is not valid for the reason \"{}\"",
                YELLOW_COLOR_LOG, ORIGINAL_COLOR_LOG, fieldName, rejectedValue, message);
        return new ErrorMessage(400, message);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMissingRequestHeaderException(MissingRequestHeaderException exception,
                                                            HttpServletRequest request) {
        String message = exception.getMessage();
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        log.warn("{}MissingRequestHeaderException: {}{}",
                YELLOW_COLOR_LOG, ORIGINAL_COLOR_LOG, message);
        return new ErrorMessage(400, message);
    }

    @ExceptionHandler({UserNotFoundException.class,
            ItemNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleNotFoundException(RuntimeException exception) {
        String message = exception.getMessage();
        log.warn("{}{}: {} {}", YELLOW_COLOR_LOG,
                exception.getClass().getSimpleName(), ORIGINAL_COLOR_LOG, message);
        return new ErrorMessage(404, message);
    }

    @ExceptionHandler(EmailIsAlreadyInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage handleEmailIsAlreadyInUseException(EmailIsAlreadyInUseException exception) {
        String message = exception.getMessage();
        log.warn("{}EmailIsAlreadyInUseException:{} {}", YELLOW_COLOR_LOG, ORIGINAL_COLOR_LOG, message);
        return new ErrorMessage(409, message);
    }

    @ExceptionHandler(NotOwnerItemException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorMessage handleNotOwnerItemException(NotOwnerItemException exception) {
        String message = exception.getMessage();
        log.warn("{}NotOwnerItemException:{} {}", YELLOW_COLOR_LOG, ORIGINAL_COLOR_LOG, message);
        return new ErrorMessage(403, message);
    }
}

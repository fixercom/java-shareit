package ru.practicum.shareit.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
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
        log.warn("{}MissingRequestHeaderException: {}{}", YELLOW_COLOR_LOG, ORIGINAL_COLOR_LOG, message);
        return new ErrorMessage(400, message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMissingServletRequestParameterException(MissingServletRequestParameterException exception,
                                                                      HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        String message = exception.getMessage();
        log.warn("{}MissingServletRequestParameterException:{} {}", YELLOW_COLOR_LOG, ORIGINAL_COLOR_LOG, message);
        return new ErrorMessage(400, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleConstraintViolationException(ConstraintViolationException exception,
                                                           HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        String message = exception.getMessage();
        log.warn("{}ConstraintViolationException:{} {}", YELLOW_COLOR_LOG, ORIGINAL_COLOR_LOG, message);
        return new ErrorMessage(400, message);
    }
}

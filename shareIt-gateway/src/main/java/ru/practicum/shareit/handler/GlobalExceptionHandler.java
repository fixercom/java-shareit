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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMethodArgumentNotValidException(MethodArgumentNotValidException exception,
                                                              HttpServletRequest request) {
        log.debug("{} request {} received: {}", request.getMethod(), request.getRequestURI(), exception.getTarget());
        String fieldName = Objects.requireNonNull(exception.getFieldError()).getField();
        Object rejectedValue = Objects.requireNonNull(exception.getFieldError()).getRejectedValue();
        String message = Objects.requireNonNull(exception.getFieldError()).getDefaultMessage();
        log.warn("MethodArgumentNotValidException: Field {}=\"{}\" is not valid for the reason \"{}\"",
                fieldName, rejectedValue, message);
        return new ErrorMessage(400, message);
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMissingRequestHeaderException(MissingRequestHeaderException exception,
                                                            HttpServletRequest request) {
        String message = exception.getMessage();
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        log.warn("MissingRequestHeaderException: {}", message);
        return new ErrorMessage(400, message);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleMissingServletRequestParameterException(MissingServletRequestParameterException exception,
                                                                      HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        String message = exception.getMessage();
        log.warn("MissingServletRequestParameterException: {}", message);
        return new ErrorMessage(400, message);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleConstraintViolationException(ConstraintViolationException exception,
                                                           HttpServletRequest request) {
        log.debug("{} request {} received", request.getMethod(), request.getRequestURI());
        String message = exception.getMessage();
        log.warn("ConstraintViolationException: {}", message);
        return new ErrorMessage(400, message);
    }
}

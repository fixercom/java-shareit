package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.sql.SQLIntegrityConstraintViolationException;
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

    @ExceptionHandler({UserNotFoundException.class,
            ItemNotFoundException.class,
            BookingNotFoundException.class,
            ItemRequestNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleNotFoundException(RuntimeException exception) {
        String message = exception.getMessage();
        log.warn("{}{}: {} {}", YELLOW_COLOR_LOG, exception.getClass().getSimpleName(), ORIGINAL_COLOR_LOG, message);
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

    @ExceptionHandler(ItemNotAvailableForBookingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleItemNotAvailableForBookingException(ItemNotAvailableForBookingException exception) {
        String message = exception.getMessage();
        log.warn("{}ItemNotAvailableForBookingException:{} {}", YELLOW_COLOR_LOG, ORIGINAL_COLOR_LOG, message);
        return new ErrorMessage(400, message);
    }

    @ExceptionHandler(NotPossibleChangeBookingStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleNotPossibleChangeBookingStatusException(
            NotPossibleChangeBookingStatusException exception) {
        String message = exception.getMessage();
        log.warn("{}NotPossibleChangeBookingStatusException:{} {}", YELLOW_COLOR_LOG, ORIGINAL_COLOR_LOG, message);
        return new ErrorMessage(400, message);
    }

    @ExceptionHandler(BookingEndDateBeforeStartDateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleBookingEndDateBeforeStartDateException(BookingEndDateBeforeStartDateException exception) {
        String message = exception.getMessage();
        log.warn("{}BookingEndDateBeforeStartDateException:{} {}", YELLOW_COLOR_LOG, ORIGINAL_COLOR_LOG, message);
        return new ErrorMessage(400, message);
    }

    @ExceptionHandler(UserDidNotBookingItemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleUserDidNotBookingItemException(UserDidNotBookingItemException exception) {
        String message = exception.getMessage();
        log.warn("{}UserDidNotBookingItemException:{} {}", YELLOW_COLOR_LOG, ORIGINAL_COLOR_LOG, message);
        return new ErrorMessage(400, message);
    }

    @ExceptionHandler(UnknownStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleUnknownStateException(UnknownStateException exception) {
        String message = exception.getMessage();
        log.warn("{}UnknownStateException:{} {}", YELLOW_COLOR_LOG, ORIGINAL_COLOR_LOG, message);
        return new ErrorMessage(400, message);
    }

    @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage handleJdbcSQLIntegrityConstraintViolationException(
            SQLIntegrityConstraintViolationException exception) {
        String message = exception.getMessage();
        if (message.contains("PUBLIC.USERS(EMAIL NULLS FIRST)")) {
            String email = message.split("'")[1];
            message = String.format("Email address %s is already used", email);
            log.warn("{}SQLIntegrityConstraintViolationException:{} {}", YELLOW_COLOR_LOG, ORIGINAL_COLOR_LOG, message);
        } else {
            log.warn("{}SQLIntegrityConstraintViolationException:{} {}", YELLOW_COLOR_LOG, ORIGINAL_COLOR_LOG, message);
        }
        return new ErrorMessage(409, message);
    }
}

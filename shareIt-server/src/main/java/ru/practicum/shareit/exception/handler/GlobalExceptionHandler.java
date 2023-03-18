package ru.practicum.shareit.exception.handler;

import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.exception.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler({UserNotFoundException.class,
            ItemNotFoundException.class,
            BookingNotFoundException.class,
            ItemRequestNotFoundException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleNotFoundException(RuntimeException exception) {
        String message = exception.getMessage();
        log.warn("{}: {}", exception.getClass().getSimpleName(), message);
        return new ErrorMessage(404, message);
    }

    @ExceptionHandler(EmailIsAlreadyInUseException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage handleEmailIsAlreadyInUseException(EmailIsAlreadyInUseException exception) {
        String message = exception.getMessage();
        log.warn("EmailIsAlreadyInUseException: {}", message);
        return new ErrorMessage(409, message);
    }

    @ExceptionHandler(NotOwnerItemException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorMessage handleNotOwnerItemException(NotOwnerItemException exception) {
        String message = exception.getMessage();
        log.warn("NotOwnerItemException: {}", message);
        return new ErrorMessage(403, message);
    }

    @ExceptionHandler(ItemNotAvailableForBookingException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleItemNotAvailableForBookingException(ItemNotAvailableForBookingException exception) {
        String message = exception.getMessage();
        log.warn("ItemNotAvailableForBookingException: {}", message);
        return new ErrorMessage(400, message);
    }

    @ExceptionHandler(NotPossibleChangeBookingStatusException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleNotPossibleChangeBookingStatusException(
            NotPossibleChangeBookingStatusException exception) {
        String message = exception.getMessage();
        log.warn("NotPossibleChangeBookingStatusException: {}", message);
        return new ErrorMessage(400, message);
    }

    @ExceptionHandler(IncorrectBookingDatesException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleIncorrectBookingDatesException(IncorrectBookingDatesException exception) {
        String message = exception.getMessage();
        log.warn("BookingEndDateBeforeStartDateException: {}", message);
        return new ErrorMessage(400, message);
    }

    @ExceptionHandler(UserDidNotBookingItemException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleUserDidNotBookingItemException(UserDidNotBookingItemException exception) {
        String message = exception.getMessage();
        log.warn("UserDidNotBookingItemException: {}", message);
        return new ErrorMessage(400, message);
    }

    @ExceptionHandler(UnknownStateException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorMessage handleUnknownStateException(UnknownStateException exception) {
        String message = exception.getMessage();
        log.warn("UnknownStateException: {}", message);
        return new ErrorMessage(400, message);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorMessage handleDataIntegrityViolationException(DataIntegrityViolationException exception) {
        String message = exception.getMessage();
        log.warn("DataIntegrityViolationException: {}", message);
        return new ErrorMessage(409, message);
    }
}

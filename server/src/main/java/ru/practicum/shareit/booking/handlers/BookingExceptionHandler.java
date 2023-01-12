package ru.practicum.shareit.booking.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.booking.exceptions.BookingDoesNotExistsException;
import ru.practicum.shareit.booking.exceptions.BookingStatusChangeException;
import ru.practicum.shareit.booking.exceptions.ItemUnavailableException;
import ru.practicum.shareit.booking.exceptions.UnsupportedStatusException;

import java.util.Objects;

@ControllerAdvice
public class BookingExceptionHandler {
    @ExceptionHandler(BookingDoesNotExistsException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleNotFoundExceptions(final RuntimeException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(BindException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleBindingExceptions(final BindException ex) {
        return Objects.requireNonNull(ex.getFieldError()).getDefaultMessage();
    }

    @ExceptionHandler(MissingRequestHeaderException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleMissingHeaderExceptions(final MissingRequestHeaderException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(SecurityException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleSecurityException(final SecurityException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(ItemUnavailableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleAvailabilityException(final ItemUnavailableException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public UnsupportedStatusException handleIllegalArgumentException() {
        return new UnsupportedStatusException("Unknown state: UNSUPPORTED_STATUS");
    }

    @ExceptionHandler(BookingStatusChangeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleBookingStatusException(final BookingStatusChangeException ex) {
        return ex.getMessage();
    }
}

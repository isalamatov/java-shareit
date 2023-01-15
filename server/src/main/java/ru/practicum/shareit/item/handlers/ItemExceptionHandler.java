package ru.practicum.shareit.item.handlers;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.shareit.item.exceptions.ItemAlreadyExistsException;
import ru.practicum.shareit.item.exceptions.ItemDoesNotExistException;
import ru.practicum.shareit.item.exceptions.UserHasToBeBookerException;
import ru.practicum.shareit.request.exceptions.ItemRequestDoesNotExistException;

@ControllerAdvice
public class ItemExceptionHandler {
    @ExceptionHandler({ItemDoesNotExistException.class, ItemRequestDoesNotExistException.class})
    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ResponseBody
    public String handleNotFoundExceptions(final RuntimeException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(ItemAlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    @ResponseBody
    public String handleAlreadyExistsExceptions(final RuntimeException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler(UserHasToBeBookerException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public String handleUserNotBookerException(final UserHasToBeBookerException ex) {
        return ex.getMessage();
    }
}

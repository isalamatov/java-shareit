package ru.practicum.shareit.user.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserDoesNotExistException extends RuntimeException {
    public UserDoesNotExistException(long id) {
        super(String.format("User with id %d does not exist", id));
    }
}

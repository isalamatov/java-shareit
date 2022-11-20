package ru.practicum.shareit.user.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String email) {
        super(String.format("User with e-mail %s already exists", email));
        log.debug("User with e-mail {} already exists", email);
    }
}

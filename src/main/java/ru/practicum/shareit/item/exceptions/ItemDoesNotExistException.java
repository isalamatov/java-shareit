package ru.practicum.shareit.item.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemDoesNotExistException extends RuntimeException {
    public ItemDoesNotExistException(long id) {
        super(String.format("User with id %d does not exist", id));
        log.debug("User with id {} does not exist", id);
    }
}

package ru.practicum.shareit.item.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ItemAlreadyExistsException extends RuntimeException {

    public ItemAlreadyExistsException(long itemId) {
        super(String.format("Item with id %d already exists", itemId));
    }
}

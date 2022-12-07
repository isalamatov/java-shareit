package ru.practicum.shareit.booking.exceptions;

public class ItemUnavailableException extends RuntimeException{
    public ItemUnavailableException(Long id) {
        super(String.format("Item with id %d is not available at the moment", id));
    }
}

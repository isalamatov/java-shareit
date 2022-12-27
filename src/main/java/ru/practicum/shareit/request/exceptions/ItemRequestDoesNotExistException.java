package ru.practicum.shareit.request.exceptions;

public class ItemRequestDoesNotExistException extends RuntimeException {
    public ItemRequestDoesNotExistException(Long requestId) {
        super(String.format("Item request with id %d does not exist", requestId));
    }
}

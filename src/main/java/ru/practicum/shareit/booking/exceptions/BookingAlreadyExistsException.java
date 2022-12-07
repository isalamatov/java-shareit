package ru.practicum.shareit.booking.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingAlreadyExistsException extends RuntimeException {

    public BookingAlreadyExistsException(long itemId) {
        super(String.format("Booking with id %d already exists", itemId));
    }
}

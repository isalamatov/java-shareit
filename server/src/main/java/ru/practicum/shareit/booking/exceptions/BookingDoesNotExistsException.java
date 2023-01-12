package ru.practicum.shareit.booking.exceptions;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BookingDoesNotExistsException extends RuntimeException {
    public BookingDoesNotExistsException(long id) {
        super(String.format("Booking with id %d does not exist", id));
    }
}

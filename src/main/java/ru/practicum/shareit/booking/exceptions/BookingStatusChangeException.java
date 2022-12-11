package ru.practicum.shareit.booking.exceptions;

public class BookingStatusChangeException extends RuntimeException {
    public BookingStatusChangeException(String s) {
        super(s);
    }
}

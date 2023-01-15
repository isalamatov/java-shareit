package ru.practicum.shareit.booking.exceptions;

public class UnsupportedStatusException extends RuntimeException {
    public final String error;

    public UnsupportedStatusException(String error) {
        this.error = error;
    }
}

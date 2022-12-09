package ru.practicum.shareit.booking.interfaces;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.enums.State;
import ru.practicum.shareit.booking.model.Booking;

import java.util.List;

public interface BookingService {
    Booking create(Booking booking);

    Booking get(Long userId, Long bookingId);

    Booking approve(Long ownerId, Long bookingId, Boolean approved);

    List<Booking> getAllByUserAndState(Long userId, State state);

    List<Booking> getAllByOwnerAndState(Long userId, State state);

    void validate(BookingDto bookingDto);
}

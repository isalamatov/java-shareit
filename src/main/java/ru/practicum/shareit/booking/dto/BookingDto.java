package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.user.User;

import java.time.ZonedDateTime;

/**
 * Booking data transfer object
 */
@Data
@Accessors(chain = true)
public class BookingDto {
    private ZonedDateTime start;
    private ZonedDateTime end;
    private ru.practicum.shareit.item.model.Item item;
    private User booker;
    private BookingStatus status;
}

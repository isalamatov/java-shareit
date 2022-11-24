package ru.practicum.shareit.booking;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.user.User;

import java.time.ZonedDateTime;

/**
 * Class, describing booking entity.
 */
@Data
@Accessors(chain = true)
public class Booking {
    private long bookingId;
    private ZonedDateTime start;
    private ZonedDateTime end;
    private ru.practicum.shareit.item.model.Item item;
    private User booker;
    private BookingStatus status;
}

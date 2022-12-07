package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

/**
 * Booking data transfer object
 */
@Data
@Accessors(chain = true)
public class BookingDto {
    private Long id;
    private String start;
    private String end;
    private Item item;
    private User booker;
    private Long itemId;
    private Long bookerId;
    private BookingStatus status;
}

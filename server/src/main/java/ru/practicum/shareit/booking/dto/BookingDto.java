package ru.practicum.shareit.booking.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.enums.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

/**
 * Booking data transfer object
 */
@Data
@Accessors(chain = true)
public class BookingDto {
    private Long id;
    private String start;
    private String end;
    private ItemDto item;
    private UserDto booker;
    private Long itemId;
    private Long bookerId;
    private BookingStatus status;
}

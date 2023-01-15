package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

/**
 * Item data transfer object, visible to end user.
 */

@Data
@Accessors(chain = true)
public class ItemDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private UserDto owner;
    private Long requestId;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
}

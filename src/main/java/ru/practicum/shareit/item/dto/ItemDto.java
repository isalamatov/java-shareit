package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comments.dto.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * Item data transfer object, visible to end user.
 */

@Data
@Accessors(chain = true)
public class ItemDto {
    private Long id;
    @NotBlank(message = "Item name should not be blank")
    private String name;
    @NotBlank(message = "Item description should not be blank")
    private String description;
    @NotNull
    private Boolean available;
    private Long request;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
}

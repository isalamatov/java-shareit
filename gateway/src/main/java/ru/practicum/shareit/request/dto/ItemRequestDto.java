package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Item request data transfer object, visible to end user.
 */
@Data
@Accessors(chain = true)
public class ItemRequestDto {
    private long id;
    @NotBlank
    private String description;
    private UserDto requestor;
    private LocalDateTime created;
    private List<ItemDto> items;
}

package ru.practicum.shareit.request.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * Item request data transfer object, visible to end user.
 */
@Data
@Accessors(chain = true)
public class ItemRequestDto {
    private long itemRequestId;
    @NotNull
    private String description;
    @NotNull
    private User requestor;
    private ZonedDateTime created;
}

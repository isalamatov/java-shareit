package ru.practicum.shareit.request;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

/**
 * Class, describing request for specific item, issued by a specific user.
 */
@Data
@Accessors(chain = true)
public class ItemRequest {
    private long ItemRequestId;
    @NotNull
    private String description;
    @NotNull
    private User requestor;
    private ZonedDateTime created;
}

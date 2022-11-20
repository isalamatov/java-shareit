package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotNull;

/**
 * Class, describing shared item or device,
 * which is used in Java code and RDBMS
 */

@Data
@Accessors(chain = true)
public class Item {
    private Long itemId;
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    private User owner;
    private ItemRequest request;
}

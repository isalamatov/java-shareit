package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;

/**
 * Item data transfer object, visible to end user.
 */

@Data
@Accessors(chain = true)
public class ItemDto {
    private Long itemId;
    private String name;
    @NotNull
    private String description;
    @NotNull
    private Boolean available;
    private Long request;
}

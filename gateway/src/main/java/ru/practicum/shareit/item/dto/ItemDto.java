package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

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
}

package ru.practicum.shareit.item.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Class, describing shared item or device,
 * which is used in Java code and RDBMS
 */

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class Item {
    private Long id;
    @NotBlank(message = "Item name should not be blank")
    private String name;
    @NotBlank(message = "Item description should not be blank")
    private String description;
    @NotNull
    private Boolean available;
    private User owner;
    private ItemRequest request;
}

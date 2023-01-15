package ru.practicum.shareit.item.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * Item partial update data transfer object, which is received from end user.
 */

@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ItemPartialUpdateDto {
    private Long id;
    private String name;
    private String description;
    private Boolean available;
    private Long requestId;
}

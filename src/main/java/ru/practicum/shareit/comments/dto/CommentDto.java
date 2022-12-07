package ru.practicum.shareit.comments.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.User;

/**
 * Item request data transfer object, visible to end user.
 */
@Data
@Accessors(chain = true)
public class CommentDto {
    private long id;
    private String text;
    private Item item;
    private User author;
}

package ru.practicum.shareit.item.interfaces;

import ru.practicum.shareit.item.model.Comment;

public interface CommentService {
    Comment create(Comment comment, Long itemId);
}

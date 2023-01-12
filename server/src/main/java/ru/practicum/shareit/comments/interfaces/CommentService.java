package ru.practicum.shareit.comments.interfaces;

import ru.practicum.shareit.comments.model.Comment;

public interface CommentService {
    Comment create(Comment comment, Long itemId);
}

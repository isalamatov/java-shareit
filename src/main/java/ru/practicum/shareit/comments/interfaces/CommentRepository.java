package ru.practicum.shareit.comments.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.comments.model.Comment;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByItem(Item item);
}

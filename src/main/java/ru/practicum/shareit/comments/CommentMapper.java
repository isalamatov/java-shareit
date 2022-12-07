package ru.practicum.shareit.comments;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.comments.dto.CommentDto;
import ru.practicum.shareit.comments.model.Comment;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "created", source = "created", dateFormat = "dd-MM-yyyy HH:mm:ss")
    @Mapping(target = "authorName", source = "author.name")
    @Mapping(target = "id", source = "commentId")
    CommentDto toDto(Comment comment);

    Comment toComment(CommentDto commentDto);

    List<CommentDto> toDto(Collection<Comment> comment);

    List<Comment> toComment(Collection<CommentDto> commentDto);
}

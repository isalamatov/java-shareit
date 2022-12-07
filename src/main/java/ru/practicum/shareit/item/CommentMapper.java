package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.Collection;
import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {
    @Mapping(target = "created", source = "created", dateFormat = "dd-MM-yyyy HH:mm:ss")
    @Mapping(target = "authorName", source = "author.name")
    CommentDto toDto(Comment comment);
    Comment toComment(CommentDto commentDto);
    List<CommentDto> toDto(Collection<Comment> comment);
    List<Comment> toComment(Collection<CommentDto> commentDto);
}

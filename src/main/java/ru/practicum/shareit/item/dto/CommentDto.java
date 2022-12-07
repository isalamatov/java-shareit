package ru.practicum.shareit.item.dto;

import lombok.Data;

import javax.annotation.Nullable;
import javax.validation.constraints.NotBlank;

@Data
public class CommentDto {
    @Nullable
    private Long commentId;
    @NotBlank
    private String text;
    private String authorName;
    private String created;
}

package ru.practicum.shareit.comments.dto;

import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.NotBlank;

@Data
public class CommentDto {
    @Nullable
    private Long id;
    @NotBlank
    private String text;
    private String authorName;
    private String created;
}

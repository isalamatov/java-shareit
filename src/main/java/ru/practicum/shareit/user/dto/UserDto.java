package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.experimental.Accessors;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * User data transfer object, visible to end-user.
 */
@Data
@Accessors(chain = true)
public class UserDto {
    private long userId;
    @NotNull
    private String name;
    @Email
    @Unique
    private String email;
}

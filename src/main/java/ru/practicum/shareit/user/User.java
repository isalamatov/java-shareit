package ru.practicum.shareit.user;

import lombok.Data;
import lombok.experimental.Accessors;
import org.checkerframework.common.aliasing.qual.Unique;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

/**
 * Class, describing user contacts and credentials, used in Java code and RDBMS
 */
@Data
@Accessors(chain = true)
public class User {
    private long userId;
    @NotNull
    private String name;
    @Email
    @Unique
    private String email;
}

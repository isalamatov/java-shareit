package ru.practicum.shareit.user;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * Class, describing user contacts and credentials, used in Java code and RDBMS
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class User {
    private long id;
    @NotBlank(message = "User name should not be blank")
    private String name;
    @NotBlank(message = "User e-mail should not be blank")
    @Email
    private String email;
}

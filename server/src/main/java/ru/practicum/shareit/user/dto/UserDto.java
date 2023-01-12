package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * User data transfer object, visible to end-user.
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UserDto {
    private long id;
    @NotBlank(message = "User name should not be blank")
    private String name;
    @NotBlank(message = "Email should not be blank")
    @Email(message = "Email should be valid")
    private String email;
}

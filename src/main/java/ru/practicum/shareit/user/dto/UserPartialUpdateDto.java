package ru.practicum.shareit.user.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.Email;

/**
 * User data transfer object, visible to end-user.
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class UserPartialUpdateDto {
    private long id;
    private String name;
    @Email(message = "Email should be valid")
    private String email;
}

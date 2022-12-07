package ru.practicum.shareit.user;

import lombok.*;
import lombok.experimental.Accessors;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

/**
 * Class, describing user contacts and credentials, used in Java code and RDBMS
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
@Entity(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;

    @NotBlank(message = "User name should not be blank")
    @Column(name = "name")
    private String name;

    @NotBlank(message = "User e-mail should not be blank")
    @Email
    @Column(name = "email", unique = true)
    private String email;
}

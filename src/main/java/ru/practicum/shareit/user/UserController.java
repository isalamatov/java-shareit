package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPartialUpdateDto;
import ru.practicum.shareit.user.interfaces.UserService;
import ru.practicum.shareit.user.model.User;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Users REST API controller, handling HTTP requests.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto create(@Valid @RequestBody UserDto userDto) {
        log.debug("Create request was received in controller {} with data {}", this.getClass(), userDto.toString());
        User user = UserMapper.toUser(userDto);
        User createdUser = userService.create(user);
        log.debug("User {} was created successfully in controller {}", createdUser.toString(), this.getClass());
        return UserMapper.toUserDto(createdUser);
    }

    @GetMapping("/{userId}")
    public UserDto get(@PathVariable Long userId) {
        log.debug("Get user request is received in controller {}, with id {}", this.getClass(), userId);
        User user = userService.get(userId);
        log.debug("User {} was retrieved successfully from controller {}", user.toString(), this.getClass());
        return UserMapper.toUserDto(user);
    }

    @GetMapping
    public List<UserDto> getAll() {
        log.debug("Get all users request is received in controller {}", this.getClass());
        List<User> users = userService.getAll();
        log.debug("All users were retrieved successfully from controller {}", this.getClass());
        return users.stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @PutMapping("/{userId}")
    public UserDto update(@PathVariable Long userId, @Valid @RequestBody UserDto userDto) {
        log.debug("Update request was received in controller {} with data {}", this.getClass(), userDto.toString());
        User user = UserMapper.toUser(userDto);
        user.setId(userId);
        User updatedUser = userService.update(user);
        log.debug("User {} was updated successfully in controller {}", updatedUser.toString(), this.getClass());
        return UserMapper.toUserDto(updatedUser);
    }

    @PatchMapping("/{userId}")
    public UserDto partialUpdate(@PathVariable Long userId, @Valid @RequestBody UserPartialUpdateDto userPartialUpdateDto) {
        log.debug("Update request was received in controller {} with data {}",
                this.getClass(),
                userPartialUpdateDto.toString());
        userPartialUpdateDto.setId(userId);
        User updatedUser = userService.partialUpdate(userPartialUpdateDto);
        log.debug("User {} was updated successfully in controller {}", updatedUser.toString(), this.getClass());
        return UserMapper.toUserDto(updatedUser);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.debug("Delete request is received in controller {}, with id {}", this.getClass(), userId);
        userService.delete(userId);
        log.debug("User with id {} was deleted successfully in controller {}", userId, this.getClass());
    }
}

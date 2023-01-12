package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserPartialUpdateDto;

import javax.validation.Valid;

/**
 * Users REST API controller, handling HTTP requests.
 */
@RestController
@RequestMapping(path = "/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {

    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> create(@Valid @RequestBody UserDto userDto) {
        log.info("Create request was received in controller {} with data {}", this.getClass(), userDto.toString());
        return userClient.create(userDto);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Object> get(@PathVariable Long userId) {
        log.info("Get user request is received in controller {}, with id {}", this.getClass(), userId);
        return userClient.getById(userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.info("Get all users request is received in controller {}", this.getClass());
        return userClient.getAll();
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Object> update(@PathVariable Long userId, @Valid @RequestBody UserDto userDto) {
        log.info("Update request was received in controller {} with data {}", this.getClass(), userDto.toString());
        return userClient.update(userId, userDto);
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<Object> partialUpdate(@PathVariable Long userId, @Valid @RequestBody UserPartialUpdateDto userPartialUpdateDto) {
        log.info("Update request was received in controller {} with data {}",
                this.getClass(),
                userPartialUpdateDto.toString());
        return userClient.partialUpdate(userId, userPartialUpdateDto);
    }

    @DeleteMapping("/{userId}")
    public void delete(@PathVariable Long userId) {
        log.info("Delete request is received in controller {}, with id {}", this.getClass(), userId);
        userClient.deleteById(userId);
    }
}

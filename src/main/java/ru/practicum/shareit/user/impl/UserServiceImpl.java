package ru.practicum.shareit.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.dto.UserPartialUpdateDto;
import ru.practicum.shareit.user.exceptions.UserAlreadyExistsException;
import ru.practicum.shareit.user.exceptions.UserDoesNotExistException;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.interfaces.UserService;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        log.debug("Create user request was received in service {}, with data {}", this.getClass(), user.toString());
        User createdUser = userRepository.save(user);
        log.debug("User {} was created successfully in service {}", createdUser, this.getClass());
        return createdUser;
    }

    @Override
    public User partialUpdate(UserPartialUpdateDto userPartialUpdateDto) {
        log.debug("Update user request was received in service {}, with data {}",
                this.getClass(),
                userPartialUpdateDto.toString());
        User updatedUser;
        if (!(userPartialUpdateDto.getEmail() == null) &&
                userRepository.existsByEmailIgnoreCase(userPartialUpdateDto.getEmail())) {
            throw new UserAlreadyExistsException(userPartialUpdateDto.getEmail());
        }
        updatedUser = userRepository.findById(userPartialUpdateDto.getId())
                .orElseThrow(() -> new UserDoesNotExistException(userPartialUpdateDto.getId()));
        Field[] fields = userPartialUpdateDto.getClass().getDeclaredFields();
        Field[] userFields = updatedUser.getClass().getDeclaredFields();
        for (Field field : fields) {
            field.setAccessible(true);
            try {
                if (field.get(userPartialUpdateDto) != null) {
                    Field userField = Arrays.stream(userFields)
                            .sequential()
                            .filter(x -> x.getName().equalsIgnoreCase(field.getName()))
                            .findFirst().get();
                    userField.setAccessible(true);
                    userField.set(updatedUser, field.get(userPartialUpdateDto));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        userRepository.save(updatedUser);
        log.debug("User {} was updated successfully in service {}", updatedUser, this.getClass());
        return updatedUser;
    }

    @Override
    public User get(Long userId) {
        log.debug("Get user request is received in service {}, with id {}", this.getClass(), userId);
        User user = userRepository.findById(userId).orElseThrow(() -> new UserDoesNotExistException(userId));
        log.debug("User {} was retrieved successfully from service {}", user.toString(), this.getClass());
        return user;
    }

    @Override
    public User update(User user) {
        log.debug("Update user request was received in service {}, with data {}", this.getClass(), user.toString());
        User updatedUser;
        if (userRepository.existsById(user.getId())) {
            updatedUser = userRepository.save(user);
        } else {
            throw new UserDoesNotExistException(user.getId());
        }
        log.debug("User {} was updated successfully in service {}", updatedUser, this.getClass());
        return updatedUser;
    }


    @Override
    public void delete(Long userId) {
        log.debug("Delete user request is received in controller {}, with id {}", this.getClass(), userId);
        User user = userRepository.findById(userId).orElseThrow(()-> new UserDoesNotExistException(userId));
        userRepository.delete(user);
        log.debug("User with id {} was deleted successfully in service {}", userId, this.getClass());
    }

    @Override
    public List<User> getAll() {
        log.debug("Get all users request is received in service {}", this.getClass());
        return userRepository.findAll();
    }
}

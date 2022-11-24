package ru.practicum.shareit.user.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserPartialUpdateDto;
import ru.practicum.shareit.user.exceptions.UserAlreadyExistsException;
import ru.practicum.shareit.user.exceptions.UserDoesNotExistException;
import ru.practicum.shareit.user.interfaces.UserRepository;
import ru.practicum.shareit.user.interfaces.UserService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public User create(User user) {
        log.debug("Create user request was received in service {}, with data {}", this.getClass(), user.toString());
        User createdUser;
        if (!userRepository.isUserExists(user.getEmail())) {
            createdUser = userRepository.create(user);
        } else {
            throw new UserAlreadyExistsException(user.getEmail());
        }
        log.debug("User {} was created successfully in service {}", createdUser.toString(), this.getClass());
        return createdUser;
    }

    @Override
    public User partialUpdate(UserPartialUpdateDto userPartialUpdateDto) {
        log.debug("Update user request was received in service {}, with data {}",
                this.getClass(),
                userPartialUpdateDto.toString());
        User updatedUser;
        if (!(userPartialUpdateDto.getEmail() == null) &&
                userRepository.isUserExists(userPartialUpdateDto.getEmail())) {
            throw new UserAlreadyExistsException(userPartialUpdateDto.getEmail());
        }
        if (userRepository.isUserExists(userPartialUpdateDto.getId())) {
            updatedUser = userRepository.partialUpdate(userPartialUpdateDto);
        } else {
            throw new UserDoesNotExistException(userPartialUpdateDto.getId());
        }
        log.debug("User {} was updated successfully in service {}", updatedUser.toString(), this.getClass());
        return updatedUser;
    }

    @Override
    public User get(Long userId) {
        log.debug("Get user request is received in service {}, with id {}", this.getClass(), userId);
        User user;
        if (userRepository.isUserExists(userId)) {
            user = userRepository.get(userId);
        } else {
            throw new UserDoesNotExistException(userId);
        }
        log.debug("User {} was retrieved successfully from service {}", user.toString(), this.getClass());
        return user;
    }

    @Override
    public User update(User user) {
        log.debug("Update user request was received in service {}, with data {}", this.getClass(), user.toString());
        User updatedUser;
        if (userRepository.isUserExists(user.getId())) {
            updatedUser = userRepository.update(user);
        } else {
            throw new UserDoesNotExistException(user.getId());
        }
        log.debug("User {} was updated successfully in service {}", updatedUser.toString(), this.getClass());
        return updatedUser;
    }


    @Override
    public void delete(Long userId) {
        log.debug("Delete user request is received in controller {}, with id {}", this.getClass(), userId);
        if (userRepository.isUserExists(userId)) {
            userRepository.delete(userId);
        } else {
            throw new UserDoesNotExistException(userId);
        }
        log.debug("User with id {} was deleted successfully in service {}", userId, this.getClass());
    }

    @Override
    public List<User> getAll() {
        log.debug("Get all users request is received in service {}", this.getClass());
        return userRepository.getAll();
    }
}

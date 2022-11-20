package ru.practicum.shareit.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.interfaces.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Repository
@Slf4j
public class UserRepositoryImpl implements UserRepository {

    private HashMap<Long, User> users;

    public UserRepositoryImpl() {
        this.users = new HashMap<>();
    }

    private long idCounter = 0L;

    @Override
    public User create(User user) {
        log.debug("Create user request was received in repository {}, with data {}", this.getClass(), user.toString());
        User createdUser = user.setUserId(idCounter++);
        users.put(createdUser.getUserId(), createdUser);
        log.debug("User {} was created successfully in repository {}", createdUser.toString(), this.getClass());
        return createdUser;
    }

    @Override
    public User get(Long userId) {
        log.debug("Get user request was received in repository {}, with data {}", this.getClass(), userId);
        User user = users.get(userId);
        log.debug("User with id {} was retrieved successfully in repository {}", userId, this.getClass());
        return user;
    }

    @Override
    public User update(User user) {
        log.debug("Update user request was received in repository {}, with data {}", this.getClass(), user.toString());
        users.put(user.getUserId(), user);
        log.debug("User {} was updated successfully in repository {}", user.toString(), this.getClass());
        return user;
    }

    @Override
    public void delete(Long userId) {
        log.debug("Delete user request was received in repository {}, with data {}", this.getClass(), userId);
        users.remove(userId);
        log.debug("User with id {} was deleted successfully from repository {}", userId, this.getClass());
    }

    @Override
    public List<User> getAll() {
        log.debug("Get all users request was received in repository {}", this.getClass());
        return new ArrayList<>(users.values());
    }

    @Override
    public boolean isUserExists(String email) {
        return users.values().stream().anyMatch(x -> x.getEmail().equalsIgnoreCase(email));
    }

    @Override
    public boolean isUserExists(Long userId) {
        return users.containsKey(userId);
    }
}

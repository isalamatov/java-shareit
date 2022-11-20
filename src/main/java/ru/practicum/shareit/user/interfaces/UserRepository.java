package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.User;

import java.util.List;

public interface UserRepository {
    User create(User user);

    User get(Long userId);

    User update(User user);

    void delete(Long userId);

    List<User> getAll();

    boolean isUserExists(String email);

    boolean isUserExists(Long userId);
}

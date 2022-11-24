package ru.practicum.shareit.user.interfaces;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserPartialUpdateDto;

import java.util.List;

public interface UserService {
    User create(User user);

    User get(Long userId);

    User update(User user);

    User partialUpdate(UserPartialUpdateDto userPartialUpdateDto);

    void delete(Long userId);

    List<User> getAll();
}

package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {
    public static User toUser(UserDto userDto) {
        return new User()
                .setUserId(userDto.getUserId())
                .setName(userDto.getName())
                .setEmail(userDto.getEmail());
    }

    public static UserDto toUserDto(User user) {
        return new UserDto().setUserId(user.getUserId())
                .setName(user.getName())
                .setEmail(user.getEmail());
    }
}

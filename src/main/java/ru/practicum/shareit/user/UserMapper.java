package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserDto;

public class UserMapper {
    public static User toUser(UserDto userDto) {
        return new User()
                .setId(userDto.getId())
                .setName(userDto.getName())
                .setEmail(userDto.getEmail());
    }

    public static UserDto toUserDto(User user) {
        return new UserDto().setId(user.getId())
                .setName(user.getName())
                .setEmail(user.getEmail());
    }
}

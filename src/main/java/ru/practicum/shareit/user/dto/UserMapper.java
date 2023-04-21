package ru.practicum.shareit.user.dto;

import lombok.NoArgsConstructor;
import ru.practicum.shareit.user.model.User;

@NoArgsConstructor
public class UserMapper {

    public static User toUser(Long id, UserDto userDto) {
        return new User(id, userDto.getName(), userDto.getEmail());
    }

    public static UserDto toUserDto(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail());
    }
}

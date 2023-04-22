package ru.practicum.shareit.user.dao;

import java.util.List;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserDao {

    UserDto createUser(UserDto user);

    UserDto updateUser(long id, UserDto user);

    UserDto findUserById(long id);

    List<UserDto> getAllUsers();

    void deleteUserById(long id);
}

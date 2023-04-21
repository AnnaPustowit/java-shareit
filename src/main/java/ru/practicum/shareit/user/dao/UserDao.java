package ru.practicum.shareit.user.dao;

import java.util.List;

import ru.practicum.shareit.user.dto.UserDto;

public interface UserDao {

    UserDto createUser(UserDto user);

    UserDto updateUser(Long id, UserDto user);

    UserDto findUserById(Long id);

    List<UserDto> getAllUsers();

    void deleteUserById(Long id);
}

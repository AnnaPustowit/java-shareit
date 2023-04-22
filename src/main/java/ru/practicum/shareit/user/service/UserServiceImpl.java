package ru.practicum.shareit.user.service;

import java.util.List;

import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dao.UserDao;
import ru.practicum.shareit.user.dto.UserDto;

@Service
public class UserServiceImpl implements UserService {

    private final UserDao userDao;

    public UserServiceImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public UserDto createUser(UserDto user) {
        return userDao.createUser(user);
    }

    @Override
    public UserDto updateUser(long id, UserDto user) {
        return userDao.updateUser(id, user);
    }

    @Override
    public UserDto findUserById(long id) {
        return userDao.findUserById(id);
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userDao.getAllUsers();
    }

    @Override
    public void deleteUserById(long id) {
        userDao.deleteUserById(id);
    }
}

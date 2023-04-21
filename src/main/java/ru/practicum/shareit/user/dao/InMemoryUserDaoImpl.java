package ru.practicum.shareit.user.dao;

import java.util.*;
import java.util.stream.Collectors;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exeption.DuplicateEmailException;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;

import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

@Slf4j
@Component
@Getter
public class InMemoryUserDaoImpl implements UserDao {

    private final Map<Long, User> users = new HashMap<>();
    private final Set<String> emails = new HashSet<>();
    private Long counter = 0L;

    @Override
    public UserDto createUser(UserDto userDto) {
        validateEmailDuplicate(userDto);
        User user = UserMapper.toUser(++counter, userDto);
        users.put(counter, user);
        emails.add(userDto.getEmail());
        log.info("Создан пользователь: {}", user);
        return toUserDto(user);
    }

    @Override
    public UserDto updateUser(Long id, UserDto user) {
        validateUser(id);
        User newUser = users.get(id);
        if (user.getEmail() != null && !newUser.getEmail().equals(user.getEmail())) {
            validateEmailDuplicate(user);
            emails.remove(newUser.getEmail());
            newUser.setEmail(user.getEmail());
            emails.add(user.getEmail());
        }
        if (user.getName() != null) {
            newUser.setName(user.getName());
        }
        users.put(id, newUser);
        log.info("Обновлен пользователь: {}", user);
        return toUserDto(newUser);
    }

    @Override
    public UserDto findUserById(Long id) {
        validateUser(id);
        return toUserDto(users.get(id));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return users.values().stream()
                .map(UserMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteUserById(Long id) {
        validateUser(id);
        emails.remove(users.get(id).getEmail());
        users.remove(id);
        log.info("Удален пользователь с id: {}", id);
    }

    private void validateUser(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Пользователь с id : " + id + " не найден");
        }
    }

    private void validateEmailDuplicate(UserDto userDto) {
        if (emails.contains(userDto.getEmail())) {
            throw new DuplicateEmailException("Данный email уже занят другим пользователем!");
        }
    }
}

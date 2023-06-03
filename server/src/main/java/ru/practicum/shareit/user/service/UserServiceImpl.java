package ru.practicum.shareit.user.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.ValidateEntityException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import static ru.practicum.shareit.user.dto.UserMapper.toUser;
import static ru.practicum.shareit.user.dto.UserMapper.toUserDto;

@Service
// @Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        //Long userId = userDto.getId();
        return toUserDto(userRepository.save(toUser(userDto)));
    }

    @Transactional
    @Override
    public UserDto updateUser(Long id, UserDto userDto) {
        User userToUpdate = toUser(userDto);
        userToUpdate.setId(id);
        User userData = userRepository.findById(id).orElseThrow(() -> new ValidateEntityException("Пользователь с id : " + id + " не найден"));
        if (userToUpdate.getName() == null) {
            userToUpdate.setName(userData.getName());
        }
        if (userToUpdate.getEmail() == null) {
            userToUpdate.setEmail(userData.getEmail());
        }
        userRepository.save(userToUpdate);
        return toUserDto(userToUpdate);
    }

    @Override
    public UserDto getUserById(Long id) {
        Optional<User> user = userRepository.findById(id);
        return toUserDto(user.orElseThrow(() -> new ValidateEntityException("Пользователь с id : " + id + " не найден")));
    }

    @Override
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream().map(UserMapper::toUserDto).collect(Collectors.toList());
    }

    @Transactional
    @Override
    public void deleteUserById(Long id) {
        userRepository.deleteById(id);
    }
}

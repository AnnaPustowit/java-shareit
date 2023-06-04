package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createUser(@RequestBody UserDto userDto) {
        UserDto newUserDto = userService.createUser(userDto);
        log.debug("Создан пользователь с id : {} ", newUserDto.getId());
        return newUserDto;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable Long userId, @RequestBody UserDto userDto) {
        UserDto newUserDto = userService.updateUser(userId, userDto);
        log.debug("Обновлен пользователь с id : {} ", userId);
        return newUserDto;
    }

    @GetMapping("/{userId}")
    public UserDto getUserById(@PathVariable Long userId) {
        UserDto newUserDto = userService.getUserById(userId);
        log.debug("Найден пользователь с id : {} ", userId);
        return newUserDto;
    }

    @GetMapping
    public List<UserDto> getAllUsers() {
        List<UserDto> usersList = userService.getAllUsers();
        log.debug("Получен список пользователей");
        return usersList;
    }

    @DeleteMapping("/{userId}")
    public void deleteUserById(@PathVariable Long userId) {
        userService.deleteUserById(userId);
        log.debug("Удален пользователь с id : {}", userId);
    }
}

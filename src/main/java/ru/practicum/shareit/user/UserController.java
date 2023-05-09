package ru.practicum.shareit.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@RestController
@RequestMapping(path = "/users")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public UserDto createUser(@Valid @RequestBody UserDto userDto) {
        UserDto newUserDto = userService.createUser(userDto);
        log.debug("Создан пользователь с id : {} ", newUserDto.getId());
        return newUserDto;
    }

    @PatchMapping("/{userId}")
    public UserDto updateUser(@PathVariable long userId, @RequestBody UserDto userDto) {
        UserDto newUserDto = userService.updateUser(userId, userDto);
        log.debug("Обновлен пользователь с id : {} ", userId);
        return newUserDto;
    }

    @GetMapping("/{userId}")
    public UserDto findUserById(@PathVariable long userId) {
        UserDto newUserDto = userService.findUserById(userId);
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
    public void deleteUserById(@PathVariable long userId) {
        userService.deleteUserById(userId);
        log.debug("Удален пользователь с id : {}", userId);
    }
}

package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exeption.ValidateEntityException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceIntegrationTest {
    private final UserRepository userRepository;
    private UserDto userDto;
    private UserService userService;

    @BeforeEach
    public void setData() {
        userService = new UserServiceImpl(userRepository);
        userDto = new UserDto();
        userDto.setName("Name");
        userDto.setEmail("name@mail.ru");
    }

    @Test
    void createUser() {
        UserDto resultUserDto = userService.createUser(userDto);

        assertThat(resultUserDto, notNullValue());
        assertThat(resultUserDto.getName(), equalTo(userDto.getName()));
        assertThat(resultUserDto.getEmail(), equalTo(userDto.getEmail()));
    }

    @Test
    void updateUser() {
        Long userId = userService.createUser(userDto).getId();
        userDto.setName("newName");
        UserDto resultUserDto = userService.updateUser(userId, userDto);

        assertThat(resultUserDto, notNullValue());
        assertThat(resultUserDto.getName(), equalTo("newName"));
    }

    @Test
    void getUserById() {
        Long userId = userService.createUser(userDto).getId();
        UserDto resultUserDto = userService.getUserById(userId);

        assertThat(resultUserDto, notNullValue());
        assertThat(resultUserDto.getId(), equalTo(userId));
    }

    @Test
    void getAllUser() {
        List<UserDto> resultUserDtoList = userService.getAllUsers();

        assertThat(resultUserDtoList, notNullValue());
        assertThat(resultUserDtoList.size(), equalTo(0));
    }

    @Test
    void deleteUser() {
        Long userId = userService.createUser(userDto).getId();
        userService.deleteUserById(userId);

        Assertions.assertThrows(ValidateEntityException.class, () -> userService.getUserById(userId));
    }
}

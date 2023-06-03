package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.exeption.ValidateEntityException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.service.UserServiceImpl;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.user.dto.UserMapper.toUser;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplTest {
    @Mock
    private final UserRepository userRepository;
    private UserService userService;
    private UserDto userDto;

    @BeforeEach
    void setData() {
        userService = new UserServiceImpl(userRepository);
        userDto = new UserDto();
        userDto.setName("Name");
        userDto.setEmail("name@mail.ru");

        when(userRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
    }

    @Test
    void createUser() {
        UserDto resultUserDto = userService.createUser(userDto);

        assertThat(resultUserDto, notNullValue());
        assertThat(resultUserDto.getName(), equalTo(userDto.getName()));
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void getUserById() {
        Long userId = 99L;

        when(userRepository.findById(userId))
                .thenReturn(Optional.empty());

        verify(userRepository, times(0)).findById(userId);
        Assertions.assertThrows(ValidateEntityException.class, () -> userService.getUserById(userId));
    }

    @Test
    void getAllUsers() {
        when(userRepository.findAll())
                .thenReturn(Collections.emptyList());

        List<UserDto> resultUserDtoList = userService.getAllUsers();

        assertThat(resultUserDtoList, notNullValue());
        assertThat("isEmpty", resultUserDtoList.isEmpty());
    }

    @Test
    void shouldNotUpdateNameOrEmailIfTheyAreNull() {
        UserDto resultUserDto = userService.createUser(userDto);
        userDto.setEmail(null);
        userDto.setName(null);
        Long userId = 1L;

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(toUser(resultUserDto)));
        UserDto resultUpdateUserDto = userService.updateUser(userId, userDto);

        assertThat(resultUpdateUserDto, notNullValue());
        assertThat(resultUpdateUserDto.getName(), equalTo(resultUserDto.getName()));
        assertThat(resultUpdateUserDto.getEmail(), equalTo(resultUserDto.getEmail()));
        verify(userRepository, times(1)).findById(userId);
    }
}

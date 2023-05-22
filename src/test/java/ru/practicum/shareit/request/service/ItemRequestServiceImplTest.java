package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;
import static ru.practicum.shareit.request.dto.ItemRequestMapper.toItemRequest;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplTest {
    @Mock
    private final ItemRequestRepository itemRequestRepository;
    @Mock
    private final UserRepository userRepository;
    @Mock
    private final ItemRepository itemRepository;
    private ItemRequestService itemRequestService;
    private ItemRequestDto itemRequestDto;
    private User user;

    @BeforeEach
    public void setData() {
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userRepository, itemRepository);
        user = new User();
        user.setId(1L);
        user.setName("Name");
        user.setEmail("name@mail.ru");
        itemRequestDto = new ItemRequestDto();
        itemRequestDto.setDescription("Аккумуляторная дрель + аккумулятор");

        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
        when(itemRequestRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when((userRepository.existsById(anyLong())))
                .thenReturn(Boolean.valueOf("true"));
    }

    @Test
    void createItemRequest() {
        ItemRequestDto resultItemRequestDto = itemRequestService.createItemRequest(1L, itemRequestDto);

        assertThat(resultItemRequestDto, notNullValue());
        assertThat(resultItemRequestDto.getDescription(), equalTo(itemRequestDto.getDescription()));
        verify(userRepository, times(1)).findById(1L);
        verify(itemRequestRepository, times(1)).save(any());
    }

    @Test
    void getAllItemRequestsByOwner() {
        when(itemRequestRepository.findAllByRequestorId(anyLong(), any()))
                .thenReturn(Collections.emptyList());
        List<ItemRequestDto> resultItemRequestDtoList = itemRequestService.getAllItemRequestsByOwner(1L);

        assertThat(resultItemRequestDtoList, notNullValue());
        assertThat("isEmpty", resultItemRequestDtoList.isEmpty());
        verify(itemRequestRepository, times(1)).findAllByRequestorId(anyLong(), any());
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void getAllItemRequests() {
        when(itemRequestRepository.findAllByRequestorIdNot(anyLong(), any()))
                .thenReturn(Page.empty());

        List<ItemRequestDto> resultItemRequestDtoList = itemRequestService.getAllItemRequests(1L, PageRequest.of(0, 10));

        assertThat(resultItemRequestDtoList, notNullValue());
        assertThat("isEmpty", resultItemRequestDtoList.isEmpty());
        verify(itemRequestRepository, times(1)).findAllByRequestorIdNot(anyLong(), any());
        verify(userRepository, times(1)).existsById(1L);
    }

    @Test
    void getItemRequestByRequestId() {
        ItemRequest itemRequest = toItemRequest(itemRequestDto, user);

        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));
        when(itemRepository.findAllByRequestId(anyLong()))
                .thenReturn(Collections.emptyList());

        ItemRequestDto resultItemRequestDto = itemRequestService.getItemRequestByRequestId(1L, 1L);

        assertThat(resultItemRequestDto, notNullValue());
        verify(itemRequestRepository, times(1)).findById(1L);
        verify(itemRepository, times(1)).findAllByRequestId(1L);
        verify(userRepository, times(1)).existsById(1L);
    }
}

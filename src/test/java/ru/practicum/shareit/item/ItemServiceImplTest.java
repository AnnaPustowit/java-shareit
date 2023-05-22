package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.service.ItemServiceImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplTest {
    @Mock
    private final ItemRepository itemRepository;
    @Mock
    private final UserRepository userRepository;
    @Mock
    private final BookingRepository bookingRepository;
    @Mock
    private final CommentRepository commentRepository;
    @Mock
    private final ItemRequestRepository itemRequestRepository;
    private ItemService itemService;
    private User user;

    @BeforeEach
    public void setData() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
        user = new User();
        user.setId(1L);
        user.setName("Name");
        user.setEmail("name@mail.ru");

        when(itemRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(anyLong()))
                .thenReturn(Optional.of(user));
    }

    @Test
    void createItem() {
        Long userId = 1L;
        User user = new User();
        user.setId(2L);

        ItemRequest itemRequest = new ItemRequest();
        itemRequest.setId(1L);
        itemRequest.setRequestor(user);
        when(itemRequestRepository.findById(anyLong()))
                .thenReturn(Optional.of(itemRequest));

        ItemDto itemDto = new ItemDto();
        itemDto.setRequestId(itemRequest.getId());

        ItemDto resultItemDto = itemService.createItem(userId, itemDto);

        assertThat(resultItemDto, notNullValue());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRequestRepository, times(1)).findById(anyLong());
    }

    @Test
    void updateItem() {
        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        ItemDto itemDto = new ItemDto();
        ItemDto resultItemDto = itemService.updateItem(user.getId(), item.getId(), itemDto);

        assertThat(resultItemDto, notNullValue());
        verify(itemRepository, times(1)).findById(anyLong());
    }

    @Test
    void getAllItems() {
        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        when(itemRepository.findAllByOwnerId(anyLong(), any()))
                .thenReturn(Page.empty());

        User booker = new User();
        booker.setId(99L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        when(bookingRepository.findAllByItemIdInAndStatusIs(anyList(), any()))
                .thenReturn(Collections.emptyList());

        List<ItemResponseDto> resultItemDtoList = itemService.getAllItems(user.getId(), PageRequest.of(0, 10));

        assertThat(resultItemDtoList, notNullValue());
        assertThat("isEmpty", resultItemDtoList.isEmpty());
        verify(itemRepository, times(1)).findAllByOwnerId(anyLong(), any());
        verify(bookingRepository, times(1)).findAllByItemIdInAndStatusIs(anyList(), any());
    }

    @Test
    void getItemById() {
        Item item = new Item();
        item.setId(1L);
        item.setOwner(user);
        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));

        User booker = new User();
        booker.setId(99L);

        Booking booking = new Booking();
        booking.setId(1L);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setItem(item);
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(booker);
        when(bookingRepository.findByItemIdAndStatusIs(anyLong(), any()))
                .thenReturn(List.of(booking));

        Comment comment = new Comment();
        comment.setId(1L);
        comment.setItem(item);
        comment.setAuthor(booker);
        when(commentRepository.findAllByItemId(anyLong()))
                .thenReturn(List.of(comment));

        ItemResponseDto resultItemResponseDto = itemService.getItemById(item.getId(), user.getId());

        assertThat(resultItemResponseDto, notNullValue());
        verify(itemRepository, times(1)).findById(anyLong());
        verify(commentRepository, times(1)).findAllByItemId(anyLong());
        verify(bookingRepository, times(1)).findByItemIdAndStatusIs(anyLong(), any());
    }

    @Test
    void searchItems() {
        when(itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableIsTrue(
                anyString(), anyString(), any()))
                .thenReturn(Page.empty());

        List<ItemDto> resultItemDtoList = itemService.searchItems(user.getId(), "Дрель", PageRequest.of(0, 10));

        assertThat(resultItemDtoList, notNullValue());
        assertThat("isEmpty", resultItemDtoList.isEmpty());
        verify(userRepository, times(1)).findById(anyLong());
        verify(itemRepository, times(1))
                .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableIsTrue(anyString(), anyString(), any());
    }

    @Test
    void createComment() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("Добавлен новый комментарий");
        Item item = new Item();
        item.setId(1L);

        when(itemRepository.findById(anyLong()))
                .thenReturn(Optional.of(item));
        when(bookingRepository.findByItemIdAndEndIsBefore(anyLong(), any()))
                .thenReturn(Collections.emptyList());

        verify(userRepository, times(0)).findById(user.getId());
        verify(itemRepository, times(0)).findById(anyLong());
        verify(bookingRepository, times(0)).findByItemIdAndEndIsBefore(anyLong(), any());
        Assertions.assertThrows(NotFoundException.class, () -> itemService.createComment(user.getId(), commentDto, 1L));
    }
}

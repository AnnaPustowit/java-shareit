package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemResponseDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static ru.practicum.shareit.item.dto.ItemMapper.toItem;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceIntegrationTest {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private ItemService itemService;
    private User user;
    private User owner;
    private ItemRequest itemRequest;
    private ItemDto itemDto;
    private Booking booking;
    private Comment comment;

    @BeforeEach
    public void setData() {
        itemService = new ItemServiceImpl(itemRepository, userRepository, bookingRepository, commentRepository, itemRequestRepository);
        user = new User();
        user.setName("Name");
        user.setEmail("name@mail.ru");
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@mail.ru");
        userRepository.save(user);
        userRepository.save(owner);
        itemRequest = new ItemRequest();
        itemRequest.setRequestor(user);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setDescription("Аккумуляторная дрель + аккумулятор");
        itemRequestRepository.save(itemRequest);
        itemDto = new ItemDto();
        itemDto.setName("Аккумуляторная дрель");
        itemDto.setDescription("Аккумуляторная");
        itemDto.setAvailable(Boolean.TRUE);
        itemDto.setRequestId(itemRequest.getId());
        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(2));
        booking.setStatus(BookingStatus.APPROVED);
        booking.setBooker(user);
        comment = new Comment();
        comment.setText("Новый комментарий");
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
    }

    @Test
    void createItem() {
        ItemDto resultItemDto = itemService.createItem(owner.getId(), itemDto);

        assertThat(resultItemDto, notNullValue());
        assertThat(resultItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(resultItemDto.getRequestId(), equalTo(itemRequest.getId()));
    }

    @Test
    void updateItem() {
        final Long itemId = itemService.createItem(owner.getId(), itemDto).getId();
        itemDto.setName("Отвертка");
        itemDto.setDescription("Новая отвертка");
        ItemDto resultItemDto = itemService.updateItem(owner.getId(), itemId, itemDto);

        assertThat(resultItemDto, notNullValue());
        assertThat(resultItemDto.getName(), equalTo(itemDto.getName()));
        assertThat(resultItemDto.getDescription(), equalTo(itemDto.getDescription()));
    }

    @Test
    void getItemById() {
        ItemDto itemResult = itemService.createItem(owner.getId(), itemDto);
        Item item = toItem(itemResult);
        item.setId(itemResult.getId());
        booking.setItem(item);
        comment.setItem(item);
        bookingRepository.save(booking);
        commentRepository.save(comment);

        ItemResponseDto resultItemResponseDto = itemService.getItemById(item.getId(), owner.getId());

        assertThat(resultItemResponseDto, notNullValue());
        assertThat(resultItemResponseDto.getComments().size(), equalTo(1));
        assertThat(resultItemResponseDto.getNextBooking().getBookerId(), equalTo(booking.getBooker().getId()));
    }

    @Test
    void getAllItems() {
        ItemDto resultItemDto = itemService.createItem(owner.getId(), itemDto);
        Item item = toItem(resultItemDto);
        item.setId(resultItemDto.getId());
        booking.setItem(item);
        bookingRepository.save(booking);

        List<ItemResponseDto> resultItemResponseDtoList = itemService.getAllItems(owner.getId(), PageRequest.of(0, 10));

        assertThat(resultItemResponseDtoList, notNullValue());
        assertThat(resultItemResponseDtoList.size(), equalTo(1));
    }

    @Test
    void searchItems() {
        itemService.createItem(owner.getId(), itemDto);
        String text = "Дрель";
        List<ItemDto> resultItemDtoList = itemService.searchItems(user.getId(), text, PageRequest.of(0, 10));

        assertThat(resultItemDtoList, notNullValue());
        assertThat(resultItemDtoList.size(), equalTo(1));
    }

    @Test
    void createComment() {
        ItemDto resultItemDto = itemService.createItem(owner.getId(), itemDto);
        Item item = toItem(resultItemDto);
        item.setId(resultItemDto.getId());
        booking.setItem(item);
        booking.setStart(LocalDateTime.now().minusDays(4));
        booking.setEnd(LocalDateTime.now().minusDays(2));
        bookingRepository.save(booking);
        CommentDto commendDto = new CommentDto();
        commendDto.setText("Новый комментарий");

        CommentResponseDto resultCommentResponseDto = itemService.createComment(user.getId(), commendDto, item.getId());

        assertThat(resultCommentResponseDto, notNullValue());
        assertThat(resultCommentResponseDto.getAuthorName(), equalTo(user.getName()));
    }
}

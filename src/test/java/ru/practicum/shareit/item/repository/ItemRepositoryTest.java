package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private CommentRepository commentRepository;
    @Autowired
    private ItemRequestRepository itemRequestRepository;
    private User owner;
    private ItemRequest itemRequest;
    final PageRequest page = PageRequest.of(0, 10);

    @BeforeEach
    public void setData() {
        User user = new User();
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
        Item item = new Item();
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная");
        item.setIsAvailable(Boolean.TRUE);
        item.setOwner(owner);
        item.setRequest(itemRequest);
        itemRepository.save(item);
        Booking booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
        Comment comment = new Comment();
        comment.setText("Новый комментарий");
        comment.setAuthor(user);
        comment.setCreated(LocalDateTime.now());
        comment.setItem(item);
        commentRepository.save(comment);
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(entityManager);
    }

    @Test
    void verifyFindAllByOwnerId() {
        Long ownerId = owner.getId();
        List<Item> resultItemList = itemRepository.findAllByOwnerId(ownerId, page)
                .stream()
                .collect(Collectors.toList());

        assertThat(resultItemList, notNullValue());
        assertThat(resultItemList.size(), equalTo(1));
    }

    @Test
    void verifyFindAllByOwnerIdList() {
        Long ownerId = owner.getId();
        List<Item> resultItemList = itemRepository.findAllByOwnerId(ownerId);

        assertThat(resultItemList, notNullValue());
        assertThat(resultItemList.size(), equalTo(1));
    }

    @Test
    void verifyFindAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableIsTrue() {
        String text = "Аккумуляторная дрель";
        List<Item> resultItemList = itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableIsTrue(
                        text, text, page)
                .stream()
                .collect(Collectors.toList());

        assertThat(resultItemList, notNullValue());
        assertThat(resultItemList.size(), equalTo(1));
    }

    @Test
    void verifyFindAllByRequestIdIn() {
        List<Item> resultItemList = itemRepository.findAllByRequestIdIn(List.of(itemRequest.getId()));

        assertThat(resultItemList, notNullValue());
        assertThat(resultItemList.size(), equalTo(1));
    }

    @Test
    void verifyFindAllByRequestId() {
        List<Item> resultItemList = itemRepository.findAllByRequestId(itemRequest.getId());

        assertThat(resultItemList, notNullValue());
        assertThat(resultItemList.size(), equalTo(1));
    }
}

package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ItemRepository itemRepository;
    private Booking booking;
    private Item item;
    private User user;
    final PageRequest page = PageRequest.of(0, 10);

    @BeforeEach
    public void setData() {
        user = new User();
        user.setName("Name");
        user.setEmail("name@mail.ru");
        userRepository.save(user);
        item = new Item();
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        item.setIsAvailable(Boolean.TRUE);
        item.setOwner(user);
        itemRepository.save(item);
        booking = new Booking();
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setEnd(LocalDateTime.now().plusDays(3));
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        bookingRepository.save(booking);
    }

    @Test
    void contextLoads() {
        Assertions.assertNotNull(entityManager);
    }

    @Test
    void verifyFindByBookerIdAndEndIsBefore() {
        Long userId = user.getId();
        LocalDateTime date = LocalDateTime.now();
        List<Booking> resultBookingList = bookingRepository.findByBookerIdAndEndIsBefore(userId, date, page)
                .stream()
                .collect(Collectors.toList());

        assertThat(resultBookingList, notNullValue());
        assertThat(resultBookingList.size(), equalTo(0));
    }

    @Test
    void verifyFindAllByBookerId() {
        Long userId = user.getId();
        List<Booking> resultBookingList = bookingRepository.findAllByBookerId(userId, page)
                .stream()
                .collect(Collectors.toList());

        assertThat(resultBookingList, notNullValue());
        assertThat(resultBookingList.size(), equalTo(1));
    }

    @Test
    void verifyFindByBookerIdAndStartIsBeforeAndEndIsAfter() {
        Long userId = user.getId();
        LocalDateTime date = LocalDateTime.now();
        List<Booking> resultBookingList = bookingRepository.findByBookerIdAndStartIsAfter(userId, date, page)
                .stream()
                .collect(Collectors.toList());

        assertThat(resultBookingList, notNullValue());
        assertThat(resultBookingList.size(), equalTo(1));
    }

    @Test
    void verifyFindAllByItemIdIn() {
        Long itemId = item.getId();
        List<Booking> resultBookingList = bookingRepository.findAllByItemIdIn(List.of(itemId), page)
                .stream()
                .collect(Collectors.toList());

        assertThat(resultBookingList, notNullValue());
        assertThat(resultBookingList.size(), equalTo(1));
    }

    @Test
    void verifyFindByItemIdInAndStartIsBeforeAndEndIsAfter() {
        Long itemId = item.getId();
        LocalDateTime date = LocalDateTime.now();
        List<Booking> resultBookingList = bookingRepository.findByItemIdInAndStartIsBeforeAndEndIsAfter(List.of(itemId), date, date, page)
                .stream()
                .collect(Collectors.toList());

        assertThat(resultBookingList, notNullValue());
        assertThat(resultBookingList.size(), equalTo(0));
    }

    @Test
    void verifyFindByItemIdInAndEndIsBefore() {
        Long itemId = item.getId();
        LocalDateTime date = LocalDateTime.now();
        List<Booking> resultBookingList = bookingRepository.findByItemIdInAndEndIsBefore(List.of(itemId), date, page)
                .stream()
                .collect(Collectors.toList());

        assertThat(resultBookingList, notNullValue());
        assertThat(resultBookingList.size(), equalTo(0));
    }

    @Test
    void verifyFindByItemIdInAndStartIsAfterAndStatusIs() {
        Long itemId = item.getId();
        LocalDateTime date = LocalDateTime.now();
        BookingStatus status = BookingStatus.WAITING;
        List<Booking> resultBookingList = bookingRepository.findByItemIdInAndStartIsAfterAndStatusIs(List.of(itemId), date, status, page)
                .stream()
                .collect(Collectors.toList());

        assertThat(resultBookingList, notNullValue());
        assertThat(resultBookingList.size(), equalTo(1));
    }

    @Test
    void verifyFindByBookerIdAndStartIsAfterAndStatusIs() {
        Long userId = user.getId();
        LocalDateTime date = LocalDateTime.now();
        BookingStatus status = BookingStatus.WAITING;
        List<Booking> resultBookingList = bookingRepository.findByBookerIdAndStartIsAfterAndStatusIs(userId, date, status, page)
                .stream()
                .collect(Collectors.toList());

        assertThat(resultBookingList, notNullValue());
        assertThat(resultBookingList.size(), equalTo(1));
    }

    @Test
    void verifyFindByItemIdInAndStartIsAfter() {
        Long itemId = item.getId();
        BookingStatus status = BookingStatus.WAITING;
        List<Booking> resultBookingList = bookingRepository.findAllByItemIdInAndStatusIs(List.of(itemId), status);

        assertThat(resultBookingList, notNullValue());
        assertThat(resultBookingList.size(), equalTo(1));
    }

    @Test
    void verifyFindByItemIdAndStatusIs() {
        Long itemId = item.getId();
        BookingStatus status = BookingStatus.WAITING;
        List<Booking> resultBookingList = bookingRepository.findByItemIdAndStatusIs(itemId, status);

        assertThat(resultBookingList, notNullValue());
        assertThat(resultBookingList.size(), equalTo(1));
    }

    @Test
    void verifyFindByItemIdAndEndIsBefore() {
        Long itemId = item.getId();
        LocalDateTime date = LocalDateTime.now();
        List<Booking> resultBookingList = bookingRepository.findByItemIdAndEndIsBefore(itemId, date);

        assertThat(resultBookingList, notNullValue());
        assertThat(resultBookingList.size(), equalTo(0));
    }

    @Test
    void verifyFindByIdAndItemOwnerId() {
        Long userId = user.getId();
        Long bookingId = booking.getId();
        Optional<Booking> resultBookingList = bookingRepository.findByIdAndItemOwnerId(bookingId, userId);

        assertThat(resultBookingList, notNullValue());
        assertThat(resultBookingList.get().getStatus(), equalTo(BookingStatus.WAITING));
    }
}
package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

@Transactional
@SpringBootTest(
        properties = "db.name=test",
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceIntegrationTest {
    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final BookingRepository bookingRepository;

    private BookingService bookingService;
    private User user;
    private User owner;
    private BookingInputDto bookingRequestDto;

    @BeforeEach
    public void setData() {
        bookingService = new BookingServiceImpl(itemRepository, userRepository, bookingRepository);
        user = new User();
        user.setName("Name");
        user.setEmail("name@mail.ru");
        owner = new User();
        owner.setName("Owner");
        owner.setEmail("owner@mail.ru");
        Item item = new Item();
        item.setOwner(owner);
        item.setIsAvailable(true);
        item.setName("Аккумуляторная дрель");
        item.setDescription("Аккумуляторная дрель + аккумулятор");
        userRepository.save(user);
        userRepository.save(owner);
        itemRepository.save(item);
        bookingRequestDto = new BookingInputDto(item.getId(), LocalDateTime.now(), LocalDateTime.now().plusDays(1));
    }

    @Test
    void createBooking() {
        BookingDto result = bookingService.createBooking(bookingRequestDto, user.getId());

        assertThat(result, notNullValue());
        assertThat(result.getItem().getId(), equalTo(bookingRequestDto.getItemId()));
    }

    @Test
    void updateBooking() {
        boolean isApproved = true;
        final Long bookingId = bookingService.createBooking(bookingRequestDto, user.getId()).getId();
        BookingDto resultBookingDto = bookingService.updateBooking(bookingId, owner.getId(), isApproved);

        assertThat(resultBookingDto, notNullValue());
        assertThat(resultBookingDto.getStatus(), equalTo(BookingStatus.APPROVED));
    }

    @Test
    void getBookingById() {
        final Long bookingId = bookingService.createBooking(bookingRequestDto, user.getId()).getId();
        BookingDto resultBookingDto = bookingService.getBookingById(user.getId(), bookingId);

        assertThat(resultBookingDto, notNullValue());
        assertThat(resultBookingDto.getId(), equalTo(bookingId));
    }

    @Test
    void getAllBookingInfo() {
        bookingService.createBooking(bookingRequestDto, user.getId());
        final String state = "ALL";
        List<BookingDto> resultBookingDtoList = bookingService.getAllBookingInfo(user.getId(), state, PageRequest.of(0, 10));

        assertThat(resultBookingDtoList, notNullValue());
        assertThat(resultBookingDtoList.size(), equalTo(1));
    }

    @Test
    void getAllOwnerBookingInfo() {
        bookingService.createBooking(bookingRequestDto, user.getId());
        final String state = "ALL";
        List<BookingDto> resultBookingDtoList = bookingService.getAllOwnerBookingInfo(owner.getId(), state, PageRequest.of(0, 10));

        assertThat(resultBookingDtoList, notNullValue());
        assertThat(resultBookingDtoList.size(), equalTo(1));
    }
}

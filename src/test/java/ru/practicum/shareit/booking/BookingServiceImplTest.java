package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.exeption.ValidateEntityException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplTest {
    @Mock
    private final ItemRepository itemRepository;

    @Mock
    private final UserRepository userRepository;

    @Mock
    private final BookingRepository bookingRepository;

    private BookingService bookingService;
    private User user;

    @BeforeEach
    public void setData() {
        bookingService = new BookingServiceImpl(itemRepository, userRepository, bookingRepository);
        user = new User();
        user.setId(1L);
        user.setName("Name");
        user.setEmail("name@mail.ru");

        when(bookingRepository.save(any()))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(userRepository.findById(user.getId()))
                .thenReturn(Optional.of(user));
    }

    @Test
    void createBooking() {
        Long userId = 1L;
        User user = new User();
        user.setId(2L);

        when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        BookingInputDto bookingInputDto = new BookingInputDto(1L, LocalDateTime.now(), LocalDateTime.now().plusDays(1));

        Item item = new Item();
        item.setId(3L);
        item.setOwner(user);
        item.setIsAvailable(true);

        when(itemRepository.findById(bookingInputDto.getItemId()))
                .thenReturn(Optional.of(item));

        BookingDto resultBookingDto = bookingService.createBooking(bookingInputDto, userId);

        assertThat(resultBookingDto, notNullValue());
        verify(userRepository, times(1)).findById(userId);
        verify(itemRepository, times(1)).findById(1L);
    }

    @Test
    void updateBooking() {

    }

    @Test
    void updateBookingStateToApproved() {
        Long bookingId = 2L;
        boolean approved = true;
        Item item = new Item();
        item.setId(3L);
        item.setOwner(user);
        item.setIsAvailable(true);
        Booking booking = new Booking();
        booking.setId(3L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setBooker(user);
        booking.setItem(item);

        when(bookingRepository.findByIdAndItemOwnerId(bookingId, user.getId()))
                .thenReturn(Optional.of(booking));

        BookingDto resultBookingDto = bookingService.updateBooking(bookingId, user.getId(), approved);

        assertThat(resultBookingDto, notNullValue());
        assertThat(resultBookingDto.getStatus(), equalTo(BookingStatus.APPROVED));
        verify(userRepository, times(1)).findById(user.getId());
        verify(bookingRepository, times(1)).findByIdAndItemOwnerId(bookingId, user.getId());
    }

    @Test
    void updateBookingStateToRejected() {
        Long bookingId = 2L;
        boolean approved = false;
        Item item = new Item();
        item.setId(3L);
        item.setOwner(user);
        item.setIsAvailable(true);
        Booking booking = new Booking();
        booking.setId(3L);
        booking.setStatus(BookingStatus.WAITING);
        booking.setStart(LocalDateTime.now().plusDays(1));
        booking.setBooker(user);
        booking.setItem(item);

        when(bookingRepository.findByIdAndItemOwnerId(bookingId, user.getId()))
                .thenReturn(Optional.of(booking));

        BookingDto resultBookingDto = bookingService.updateBooking(bookingId, user.getId(), approved);

        assertThat(resultBookingDto, notNullValue());
        assertThat(resultBookingDto.getStatus(), equalTo(BookingStatus.REJECTED));
        verify(userRepository, times(1)).findById(user.getId());
        verify(bookingRepository, times(1)).findByIdAndItemOwnerId(bookingId, user.getId());
    }

    @Test
    void getBookingById() {
        Long bookingId = 2L;

        when(bookingRepository.findById(bookingId))
                .thenReturn(Optional.empty());

        verify(userRepository, times(0)).findById(user.getId());
        verify(bookingRepository, times(0)).findById(bookingId);
        Assertions.assertThrows(ValidateEntityException.class, () -> bookingService.getBookingById(user.getId(), bookingId));
    }

    @Test
    void getAllBookingInfo() {
        when(bookingRepository.findAllByBookerId(any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> resultBookingDtoList = bookingService.getAllBookingInfo(1L, "ALL", PageRequest.of(0, 10));

        assertThat(resultBookingDtoList, notNullValue());
        assertThat("isEmpty", resultBookingDtoList.isEmpty());
        verify(bookingRepository, times(1)).findAllByBookerId(any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }

    @Test
    void getAllOwnerBookingInfo() {
        when(bookingRepository.findAllByItemIdIn(any(), any()))
                .thenReturn(Page.empty());

        List<BookingDto> resultBookingDtoList = bookingService.getAllOwnerBookingInfo(1L, "ALL", PageRequest.of(0, 10));

        assertThat(resultBookingDtoList, notNullValue());
        assertThat("isEmpty", resultBookingDtoList.isEmpty());
        verify(bookingRepository, times(1)).findAllByItemIdIn(any(), any());
        verify(userRepository, times(1)).findById(user.getId());
    }
}

package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidateEntityException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static ru.practicum.shareit.booking.dto.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingDto;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto createBooking(BookingInputDto bookingInputDto, Long userId) {
        validateDate(bookingInputDto);
        final Long itemId = bookingInputDto.getItemId();
        final User user = validateUser(userId);
        final Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new ValidateEntityException("Вещь с id : " + itemId + " не найдена."));
        if (!item.getIsAvailable())
            throw new NotFoundException("Вещь не доступна для бронирования.");
        final Long id = item.getOwner().getId();
        if (Objects.equals(id, userId))
            throw new ValidateEntityException("Бронирование своей же вещи запрещено.");
        final Booking booking = toBooking(bookingInputDto, item, user);
        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Long bookingId, Long userId, boolean isApproved) {
        final User user = validateUser(userId);
        Booking booking = bookingRepository.findByIdAndItemOwnerId(bookingId, userId)
                .orElseThrow(() -> new ValidateEntityException("Бронь с id : " + bookingId + " не найдена."));
        if (!booking.getStatus().equals(BookingStatus.WAITING))
            throw new NotFoundException("Статус должен быть WAITING.");
        if (isApproved)
            booking.setStatus(BookingStatus.APPROVED);
        else
            booking.setStatus(BookingStatus.REJECTED);
        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto getBookingById(Long userId, Long bookingId) {
        final User user = validateUser(userId);
        return toBookingDto(bookingRepository.findById(bookingId)
                .filter(b -> Objects.equals(b.getBooker().getId(), userId) || Objects.equals(b.getItem().getOwner().getId(), userId))
                .orElseThrow(() -> new ValidateEntityException("Бронирование вещи с id : " + bookingId + " не найдено.")));
    }

    @Override
    public List<BookingDto> getAllBookingInfo(Long userId, String state, PageRequest page) {
        final BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new NotFoundException("Unknown state: " + state));
        final User user = validateUser(userId);
        final LocalDateTime date = LocalDateTime.now();
        Page<Booking> bookings;
        if (bookingState.equals(BookingState.ALL)) {
            bookings = bookingRepository.findAllByBookerId(userId, page);
        } else if (bookingState.equals(BookingState.CURRENT)) {
            bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, date, date, page);
        } else if (bookingState.equals(BookingState.PAST)) {
            bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, date, page);
        } else if (bookingState.equals(BookingState.FUTURE)) {
            bookings = bookingRepository.findByBookerIdAndStartIsAfter(userId, date, page);
        } else if (bookingState.equals(BookingState.WAITING)) {
            bookings = bookingRepository.findByBookerIdAndStartIsAfterAndStatusIs(userId, date, BookingStatus.WAITING, page);
        } else if (bookingState.equals(BookingState.REJECTED)) {
            bookings = bookingRepository.findByBookerIdAndStartIsAfterAndStatusIs(userId, date, BookingStatus.REJECTED, page);
        } else {
            return emptyList();
        }
        return bookings
                .map(BookingMapper::toBookingDto)
                .getContent();

    }

    @Override
    public List<BookingDto> getAllOwnerBookingInfo(Long userId, String state, PageRequest page) {
        final BookingState bookingState = BookingState.from(state)
                .orElseThrow(() -> new NotFoundException("Unknown state: " + state));
        final User user = validateUser(userId);
        final List<Long> itemIdList = itemRepository.findAllByOwnerId(userId)
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        final LocalDateTime date = LocalDateTime.now();
        Page<Booking> bookings;
        if (bookingState.equals(BookingState.ALL)) {
            bookings = bookingRepository.findAllByItemIdIn(itemIdList, page);
        } else if (bookingState.equals(BookingState.CURRENT)) {
            bookings = bookingRepository.findByItemIdInAndStartIsBeforeAndEndIsAfter(itemIdList, date, date, page);
        } else if (bookingState.equals(BookingState.PAST)) {
            bookings = bookingRepository.findByItemIdInAndEndIsBefore(itemIdList, date, page);
        } else if (bookingState.equals(BookingState.FUTURE)) {
            bookings = bookingRepository.findByItemIdInAndStartIsAfter(itemIdList, date, page);
        } else if (bookingState.equals(BookingState.WAITING)) {
            bookings = bookingRepository.findByItemIdInAndStartIsAfterAndStatusIs(itemIdList, date, BookingStatus.WAITING, page);
        } else if (bookingState.equals(BookingState.REJECTED)) {
            bookings = bookingRepository.findByItemIdInAndStartIsAfterAndStatusIs(itemIdList, date, BookingStatus.REJECTED, page);
        } else {
            return emptyList();
        }
        return bookings
                .map(BookingMapper::toBookingDto)
                .getContent();
    }

    private void validateDate(BookingInputDto bookingInputDto) {
        if (bookingInputDto.getEnd().isBefore(bookingInputDto.getStart()) || bookingInputDto.getEnd().equals(bookingInputDto.getStart()))
            throw new NotFoundException("Дата окончания бронирования не может быть позже даты старта или начинаться в одно время.");
    }

    private User validateUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ValidateEntityException("Пользователь с id : " + userId + " не найден."));
    }
}



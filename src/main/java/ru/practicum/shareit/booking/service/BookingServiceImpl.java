package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingState;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingJsonDto;
import ru.practicum.shareit.booking.dto.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidateEntityException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static ru.practicum.shareit.booking.dto.BookingMapper.toBooking;
import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingDto;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    public BookingDto createBooking(BookingJsonDto bookingJsonDto, Long userId) {
        validate(bookingJsonDto);
        final long itemId = bookingJsonDto.getItemId();
        final User user = userRepository.findById(userId).orElseThrow(() -> new ValidateEntityException("Пользователь с id : " + userId + " не найден."));
        final Item item = itemRepository.findById(itemId).orElseThrow(() -> new ValidateEntityException("Вещь с id : " + itemId + " не найдена."));
        if (!item.getIsAvailable()) throw new NotFoundException("Вещь не доступна для бронирования.");
        final Long id = item.getOwner().getId();
        if (Objects.equals(id, userId)) throw new ValidateEntityException("Бронирование своей же вещи запрещено.");
        final Booking booking = toBooking(bookingJsonDto, item, user);
        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto updateBooking(Long bookingId, Long userId, boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId).orElseThrow(() -> new ValidateEntityException("Бронирование вещи с id : " + bookingId + " не обнаружено."));
        final Long id = booking.getItem().getOwner().getId();
        if (!Objects.equals(id, userId))
            throw new ValidateEntityException("Пользователь с id : " + userId + " не может обновить статус вещи.");
        if (!booking.getStatus().equals(BookingStatus.WAITING))
            throw new NotFoundException("Статус запроса должен быть WAITING.");
        if (isApproved) booking.setStatus(BookingStatus.APPROVED);
        else booking.setStatus(BookingStatus.REJECTED);
        return toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto findBookingById(Long userId, Long bookingId) {
        return toBookingDto(bookingRepository.findById(bookingId).filter(b -> Objects.equals(b.getBooker().getId(), userId) || Objects.equals(b.getItem().getOwner().getId(), userId)).orElseThrow(() -> new ValidateEntityException("Бронирование вещи с id : " + bookingId + " не найдено.")));
    }

    @Override
    public List<BookingDto> getAllBookingInfo(Long userId, String state) {
        final BookingState bookingState = BookingState.from(state).orElseThrow(() -> new NotFoundException("Unknown state: " + state));
        final User user = userRepository.findById(userId).orElseThrow(() -> new ValidateEntityException("Пользователь с id : " + userId + " не найден."));
        final LocalDateTime date = LocalDateTime.now();
        final Sort sort = Sort.by("start").descending();
        List<Booking> bookings;
        if (bookingState.equals(BookingState.ALL)) {
            bookings = bookingRepository.findAllByBookerId(userId, sort);
        } else if (bookingState.equals(BookingState.CURRENT)) {
            bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfter(userId, date, date, sort);
        } else if (bookingState.equals(BookingState.PAST)) {
            bookings = bookingRepository.findByBookerIdAndEndIsBefore(userId, date, sort);
        } else if (bookingState.equals(BookingState.FUTURE)) {
            bookings = bookingRepository.findByBookerIdAndStartIsAfter(userId, date, sort);
        } else if (bookingState.equals(BookingState.WAITING)) {
            bookings = bookingRepository.findByBookerIdAndStartIsAfterAndStatusIs(userId, date, sort, BookingStatus.WAITING);
        } else if (bookingState.equals(BookingState.REJECTED)) {
            bookings = bookingRepository.findByBookerIdAndStartIsAfterAndStatusIs(userId, date, sort, BookingStatus.REJECTED);
        } else {
            return emptyList();
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllOwnerBookingInfo(Long userId, String state) {
        final BookingState bookingState = BookingState.from(state).orElseThrow(() -> new NotFoundException("Unknown state: " + state));
        final User user = userRepository.findById(userId).orElseThrow(() -> new ValidateEntityException("Пользователь с id : " + userId + " не найден."));
        final List<Long> itemIdList = itemRepository.findAllByOwnerId(userId).stream().map(Item::getId).collect(Collectors.toList());
        final LocalDateTime date = LocalDateTime.now();
        final Sort sort = Sort.by("start").descending();
        List<Booking> bookings;
        if (bookingState.equals(BookingState.ALL)) {
            bookings = bookingRepository.findAllByItemIdIn(itemIdList, sort);
        } else if (bookingState.equals(BookingState.CURRENT)) {
            bookings = bookingRepository.findByItemIdInAndStartIsBeforeAndEndIsAfter(itemIdList, date, date, sort);
        } else if (bookingState.equals(BookingState.PAST)) {
            bookings = bookingRepository.findByItemIdInAndEndIsBefore(itemIdList, date, sort);
        } else if (bookingState.equals(BookingState.FUTURE)) {
            bookings = bookingRepository.findByItemIdInAndStartIsAfter(itemIdList, date, sort);
        } else if (bookingState.equals(BookingState.WAITING)) {
            bookings = bookingRepository.findByItemIdInAndStartIsAfterAndStatusIs(itemIdList, date, sort, BookingStatus.WAITING);
        } else if (bookingState.equals(BookingState.REJECTED)) {
            bookings = bookingRepository.findByItemIdInAndStartIsAfterAndStatusIs(itemIdList, date, sort, BookingStatus.REJECTED);
        } else {
            return emptyList();
        }
        return bookings.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    private void validate(BookingJsonDto bookingJsonDto) {
        if (bookingJsonDto.getEnd().isBefore(bookingJsonDto.getStart()) || bookingJsonDto.getEnd().equals(bookingJsonDto.getStart()))
            throw new NotFoundException("Дата окончания бронирования не может быть позже даты старта или начинаться в одно время.");
    }
}

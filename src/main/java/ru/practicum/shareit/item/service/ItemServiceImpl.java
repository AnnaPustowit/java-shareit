package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exeption.NotFoundException;
import ru.practicum.shareit.exeption.ValidateEntityException;
import ru.practicum.shareit.item.dto.*;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.shareit.booking.dto.BookingMapper.toBookingItemDto;
import static ru.practicum.shareit.item.dto.CommentMapper.toComment;
import static ru.practicum.shareit.item.dto.CommentMapper.toCommentResponseDto;
import static ru.practicum.shareit.item.dto.ItemMapper.*;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Transactional
    @Override
    public ItemDto createItem(Long userId, ItemDto itemDto) {
        final Item item = toItem(itemDto);
        final User user = userRepository.findById(userId).orElseThrow(() -> new ValidateEntityException("Пользователь с id : " + userId + " не найден."));
        item.setOwner(user);
        return toItemDto(itemRepository.save(item));
    }

    @Transactional
    @Override
    public ItemDto updateItem(Long userId, Long itemId, ItemDto itemDto) {
        final Item itemUpdate = itemRepository.findById(itemId).orElseThrow(() -> new ValidateEntityException("Вещь с id : " + itemId + " не найдена."));
        if (!userId.equals(itemUpdate.getOwner().getId())) {
            throw new ValidateEntityException("Владелец вещи с id : " + userId + " указан не верно.");
        }
        if (itemDto.getName() != null) itemUpdate.setName(itemDto.getName());
        if (itemDto.getDescription() != null) itemUpdate.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) itemUpdate.setIsAvailable(itemDto.getAvailable());
        final Item item = itemRepository.save(itemUpdate);
        return toItemDto(item);
    }

    @Override
    public List<ItemResponseDto> getAllItems(Long userId) {
        final List<ItemResponseDto> itemsList = itemRepository.findAllByOwnerId(userId).stream().map(ItemMapper::toItemResponseDto).collect(Collectors.toList());
        final List<Long> itemsId = itemsList
                .stream()
                .map(ItemResponseDto::getId)
                .collect(Collectors.toList());
        final List<Booking> bookingList = bookingRepository.findAllByItemIdIn(itemsId);
        return itemsList.stream().map(itemsDto -> saveBookingsData(itemsDto, bookingList)).collect(Collectors.toList());
    }

    @Override
    public ItemResponseDto findItemById(Long itemId, Long userId) {
        final Item item = itemRepository.findById(itemId).orElseThrow(() -> new ValidateEntityException("Вещь с id : " + itemId + " не найдена."));
        final List<CommentResponseDto> comments = commentRepository.findAllByItemId(itemId)
                .stream()
                .map(CommentMapper::toCommentResponseDto)
                .collect(Collectors.toList());
        final ItemResponseDto itemResponseDto = toItemResponseDto(item);
        itemResponseDto.setComments(comments);
        final Long id = item.getOwner().getId();
        if (Objects.equals(userId, id)) {
            final List<Booking> bookingList = bookingRepository.findByItemId(itemId);
            return saveBookingsData(itemResponseDto, bookingList);
        }
        return itemResponseDto;
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text == null || text.isBlank()) return Collections.emptyList();
        return itemRepository.findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndIsAvailableIsTrue(text, text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public CommentResponseDto createComment(Long userId, CommentDto commentDto, Long itemId) {
        final Comment comment = toComment(commentDto);
        final User author = userRepository.findById(userId).orElseThrow(() -> new ValidateEntityException("Пользователь с id : " + userId + " не найден."));
        final Item item = itemRepository.findById(itemId).orElseThrow(() -> new ValidateEntityException("Вещь с id : " + itemId + " не найдена."));
        List<Booking> bookings = bookingRepository.findByItemIdAndEndIsBefore(itemId, comment.getCreated())
                .stream()
                .filter(booking -> Objects.equals(booking.getBooker().getId(), userId))
                .collect(Collectors.toList());
        if (bookings.isEmpty()) {
            throw new NotFoundException("Пользователь не может оставить отзыв для вещи.");
        }
        comment.setAuthor(author);
        comment.setItem(item);
        commentRepository.save(comment);
        return toCommentResponseDto(comment);
    }

    private ItemResponseDto saveBookingsData(ItemResponseDto itemsDto, List<Booking> bookingList) {
        final LocalDateTime time = LocalDateTime.now();
        Optional<Booking> bookingLast = bookingList
                .stream()
                .filter(booking -> Objects.equals(booking.getItem().getId(), itemsDto.getId()))
                .sorted(Comparator.comparing(Booking::getEnd).reversed())
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .filter(booking -> booking.getStart().isBefore(time))
                .limit(1)
                .findAny();
        bookingLast.ifPresent(booking -> itemsDto.setLastBooking(toBookingItemDto(booking)));

        Optional<Booking> bookingNext = bookingList
                .stream()
                .filter(booking -> Objects.equals(booking.getItem().getId(), itemsDto.getId()))
                .sorted(Comparator.comparing(Booking::getStart))
                .filter(booking -> booking.getStatus().equals(BookingStatus.APPROVED))
                .filter(booking -> booking.getStart().isAfter(time))
                .limit(1)
                .findAny();
        bookingNext.ifPresent(booking -> itemsDto.setNextBooking(toBookingItemDto(booking)));
        return itemsDto;
    }
}

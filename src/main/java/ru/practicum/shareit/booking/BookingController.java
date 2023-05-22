package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/bookings")
@Validated
public class BookingController {
    private final BookingService bookingService;

    private final Sort sort = Sort.by("start").descending();//

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @Valid @RequestBody BookingInputDto bookingJsonDto) {
        BookingDto bookingDto = bookingService.createBooking(bookingJsonDto, userId);
        log.debug("Создана бронь на вещь с id : {}", bookingJsonDto.getItemId());
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId,
                                    @PathVariable Long bookingId,
                                    @RequestParam(value = "approved") String approved) {
        boolean isApproved = approved.equals("true");
        BookingDto bookingDto = bookingService.updateBooking(bookingId, userId, isApproved);
        log.debug("Обновлен статус брони на вещь с id : {}", bookingId);
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader("X-Sharer-User-Id") Long userId,
                                     @PathVariable Long bookingId) {
        BookingDto bookingDto = bookingService.getBookingById(userId, bookingId);
        log.debug("Получена бронь с id : {}", bookingId);
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> getAllBookingInfo(@RequestHeader("X-Sharer-User-Id") Long userId,
                                              @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
                                              @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                              @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sort);
        List<BookingDto> bookingDtoList = bookingService.getAllBookingInfo(userId, state, page);
        log.debug("Получен список бронирований пользователя с id : {}", userId);
        return bookingDtoList;
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwnerBookingInfo(@RequestHeader("X-Sharer-User-Id") Long userId,
                                                   @RequestParam(value = "state", defaultValue = "ALL", required = false) String state,
                                                   @PositiveOrZero @RequestParam(value = "from", defaultValue = "0") Integer from,
                                                   @Positive @RequestParam(value = "size", defaultValue = "10") Integer size) {
        final PageRequest page = PageRequest.of(from > 0 ? from / size : 0, size, sort);
        List<BookingDto> bookingDtoList = bookingService.getAllOwnerBookingInfo(userId, state, page);
        log.debug("Получен список бронирований для вещей пользователя с id : {}", userId);
        return bookingDtoList;
    }
}

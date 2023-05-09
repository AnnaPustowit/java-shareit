package ru.practicum.shareit.booking;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.Valid;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@RestController
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {
    private final BookingService bookingService;

    @Autowired
    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public BookingDto createBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @Valid @RequestBody BookingInputDto bookingJsonDto) {
        BookingDto bookingDto = bookingService.createBooking(bookingJsonDto, userId);
        log.debug("Создана бронь на вещь с id : {}", bookingJsonDto.getItemId());
        return bookingDto;
    }

    @PatchMapping("/{bookingId}")
    public BookingDto updateBooking(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId, @RequestParam(value = "approved") String approved) {
        boolean isApproved = approved.equals("true");
        BookingDto bookingDto = bookingService.updateBooking(bookingId, userId, isApproved);
        log.debug("Обновлен статус брони на вещь с id : {}", bookingId);
        return bookingDto;
    }

    @GetMapping("/{bookingId}")
    public BookingDto findBookingById(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable Long bookingId) {
        BookingDto bookingDto = bookingService.findBookingById(userId, bookingId);
        log.debug("Получена бронь с id : {}", bookingId);
        return bookingDto;
    }

    @GetMapping
    public List<BookingDto> getAllBookingInfo(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(value = "state", defaultValue = "ALL", required = false) String state) {
        List<BookingDto> bookingDtoList = bookingService.getAllBookingInfo(userId, state);
        log.debug("Получен список бронирований пользователя с id : {}", userId);
        return bookingDtoList;
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllOwnerBookingInfo(@RequestHeader("X-Sharer-User-Id") Long userId, @RequestParam(value = "state", defaultValue = "ALL", required = false) String state) {
        List<BookingDto> bookingDtoList = bookingService.getAllOwnerBookingInfo(userId, state);
        log.debug("Получен список бронирования для вещей пользователя с id : {}", userId);
        return bookingDtoList;
    }
}

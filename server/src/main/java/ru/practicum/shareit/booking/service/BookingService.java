package ru.practicum.shareit.booking.service;

import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingInputDto;

import java.util.List;

public interface BookingService {
    BookingDto createBooking(BookingInputDto bookingJsonDto, Long userId);

    BookingDto updateBooking(Long bookingId, Long userId, boolean isApproved);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getAllBookingInfo(Long userId, String state, PageRequest page);

    List<BookingDto> getAllOwnerBookingInfo(Long userId, String state, PageRequest page);
}

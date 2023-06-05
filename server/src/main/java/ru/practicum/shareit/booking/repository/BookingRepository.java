package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Page<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime date, PageRequest page);

    Page<Booking> findAllByBookerId(Long bookerId, PageRequest page);

    Page<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime date, LocalDateTime date1, PageRequest page);

    Page<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime date, PageRequest page);

    Page<Booking> findAllByItemIdIn(List<Long> itemId, PageRequest page);

    Page<Booking> findByItemIdInAndStartIsBeforeAndEndIsAfter(List<Long> itemId, LocalDateTime date, LocalDateTime date1, PageRequest page);

    Page<Booking> findByItemIdInAndEndIsBefore(List<Long> itemId, LocalDateTime date, PageRequest page);

    Page<Booking> findByItemIdInAndStartIsAfterAndStatusIs(List<Long> itemId, LocalDateTime date, BookingStatus bookingStatus, PageRequest page);

    Page<Booking> findByBookerIdAndStartIsAfterAndStatusIs(Long userId, LocalDateTime date, BookingStatus bookingStatus, PageRequest page);

    Page<Booking> findByItemIdInAndStartIsAfter(List<Long> itemIdList, LocalDateTime date, PageRequest page);

    List<Booking> findAllByItemIdInAndStatusIs(List<Long> itemId, BookingStatus status);

    List<Booking> findByItemIdAndStatusIs(Long itemId, BookingStatus status);

    List<Booking> findByItemIdAndEndIsBefore(Long itemId, LocalDateTime date);

    Optional<Booking> findByIdAndItemOwnerId(Long bookingId, Long userId);
}

package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findByBookerIdAndEndIsBefore(Long bookerId, LocalDateTime end, Sort sort);

    List<Booking> findAllByBookerId(Long bookerId, Sort sort);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfter(Long bookerId, LocalDateTime start, LocalDateTime end, Sort sort);

    List<Booking> findByBookerIdAndStartIsAfter(Long bookerId, LocalDateTime start, Sort sort);

    List<Booking> findAllByItemIdIn(List<Long> itemId, Sort sort);

    List<Booking> findByItemIdInAndStartIsBeforeAndEndIsAfter(List<Long> itemId, LocalDateTime date, LocalDateTime date1, Sort sort);

    List<Booking> findByItemIdInAndEndIsBefore(List<Long> itemId, LocalDateTime date, Sort sort);

    List<Booking> findByItemIdInAndStartIsAfterAndStatusIs(List<Long> itemId, LocalDateTime date, Sort sort, BookingStatus bookingStatus);

    List<Booking> findByBookerIdAndStartIsAfterAndStatusIs(Long userId, LocalDateTime date, Sort sort, BookingStatus bookingStatus);

    List<Booking> findByItemIdInAndStartIsAfter(List<Long> itemIdList, LocalDateTime date, Sort sort);

    List<Booking> findAllByItemIdIn(List<Long> itemId);

    List<Booking> findByItemId(Long itemId);

    List<Booking> findByItemIdAndEndIsBefore(Long itemId, LocalDateTime date);
}

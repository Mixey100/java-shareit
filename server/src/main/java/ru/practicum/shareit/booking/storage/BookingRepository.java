package ru.practicum.shareit.booking.storage;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.model.Booking;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    List<Booking> findAllByBookerId(Long userId);

    List<Booking> findAllByBookerIdAndItemIdAndEndBeforeOrderByStartDesc(Long userId, Long itemId, LocalDateTime now);

    List<Booking> findAllByItemIdAndStatusAndEndBeforeOrderByEndDesc(Long itemId, Status status, LocalDateTime now);

    List<Booking> findAllByItemIdAndStartAfterOrderByStartAsc(Long itemId, LocalDateTime now);

    List<Booking> findByItemIdInAndStatusAndEndBeforeOrderByEndDesc(List<Long> itemIds, Status status, LocalDateTime now);

    List<Booking> findByItemIdInAndStartAfterOrderByStartAsc(List<Long> itemIds, LocalDateTime now);

    List<Booking> findAllByBookerIdAndEndBeforeOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndEndAfterOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStartAfterOrderByStartDesc(Long userId, LocalDateTime now);

    List<Booking> findAllByBookerIdAndStatusOrderByStartDesc(Long userId, Status status);

    List<Booking> findByItemIdInOrderByStartDesc(List<Long> itemIds);

    List<Booking> findByItemIdInAndEndBeforeOrderByStartDesc(List<Long> itemIds, LocalDateTime now);

    List<Booking> findByItemIdInAndEndAfterOrderByStartDesc(List<Long> itemIds, LocalDateTime now);

    List<Booking> findByItemIdInAndStartAfterOrderByStartDesc(List<Long> itemIds, LocalDateTime now);

    List<Booking> findByItemIdInAndStatusOrderByStartDesc(List<Long> itemIds, Status status);
}


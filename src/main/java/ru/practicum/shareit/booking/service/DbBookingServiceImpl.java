package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.enums.State;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.storage.BookingRepository;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DbBookingServiceImpl implements BookingService {

    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public BookingDto createBooking(BookingDtoRequest bookingDtoRequest, Long userId) {
        log.info("Начало бронирования");
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Item item = itemRepository.findById(bookingDtoRequest.getItemId())
                .orElseThrow(() -> new NotFoundException("Вещь с id = " + bookingDtoRequest.getItemId() + " не найдена"));
        if (!item.getAvailable()) {
            throw new IllegalStateException("Вещь " + item.getId() + " недоступна для бронирования");
        }
        Booking booking = bookingRepository.save(BookingMapper.mapToBooking(bookingDtoRequest, user, item));
        log.info("Запрос бронирования с id = {} добавлен", booking.getId());
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto approveBooking(Long bookingId, Boolean approved, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));
        Long ownerId = booking.getItem().getOwner().getId();
        if (!Objects.equals(ownerId, userId)) {
            throw new ValidationException("Пользователь с id " + userId + " не является владельцем вещи "
                    + booking.getItem());
        }
        if (!Objects.equals(booking.getStatus(), Status.WAITING)) {
            throw new IllegalStateException("Бронирование подтверждено или отклонено");
        }
        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }
        booking = bookingRepository.save(booking);
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public BookingDto getBookingById(Long bookingId, Long userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException("Бронирование с id = " + bookingId + " не найдено"));
        return BookingMapper.mapToBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookingsByUserId(Long userId, State state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case PAST -> bookingRepository.findAllByBookerIdAndEndBeforeOrderByStartDesc(userId, now);
            case CURRENT -> bookingRepository.findAllByBookerIdAndEndAfterOrderByStartDesc(userId, now);
            case FUTURE -> bookingRepository.findAllByBookerIdAndStartAfterOrderByStartDesc(userId, now);
            case WAITING, REJECTED -> bookingRepository.findAllByBookerIdAndStatusOrderByStartDesc(userId,
                    Status.valueOf(state.toString()));
            default -> bookingRepository.findAllByBookerId(userId);
        };
        return BookingMapper.mapToBookingDto(bookings);
    }

    @Override
    public List<BookingDto> getBookingsByItemsOwner(Long userId, State state) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("Пользователь с id = " + userId + " не найден"));
        List<Long> itemIds = itemRepository.findByOwnerIdOrderById(userId)
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        if (itemIds.isEmpty()) {
            return Collections.emptyList();
        }
        LocalDateTime now = LocalDateTime.now();
        List<Booking> bookings = switch (state) {
            case PAST -> bookingRepository.findByItemIdInAndEndBeforeOrderByStartDesc(itemIds, now);
            case CURRENT -> bookingRepository.findByItemIdInAndEndAfterOrderByStartDesc(itemIds, now);
            case FUTURE -> bookingRepository.findByItemIdInAndStartAfterOrderByStartDesc(itemIds, now);
            case WAITING, REJECTED -> bookingRepository.findByItemIdInAndStatusOrderByStartDesc(itemIds,
                    Status.valueOf(state.toString()));
            default ->  bookingRepository.findByItemIdInOrderByStartDesc(itemIds);
        };
        return bookings.stream()
                .map(BookingMapper::mapToBookingDto)
                .toList();
    }
}

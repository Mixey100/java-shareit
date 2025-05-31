package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.enums.State;

import java.util.List;

public interface BookingService {

    BookingDto createBooking(BookingDtoRequest bookingDtoRequest, Long userId);

    BookingDto approveBooking(Long bookingId, Boolean approve, Long userId);

    BookingDto getBookingById(Long bookingId, Long userId);

    List<BookingDto> getBookingsByUserId(Long userId, State state);

    List<BookingDto> getBookingsByItemsOwner(Long userId, State state);
}

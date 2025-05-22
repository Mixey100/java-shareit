package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;

public class BookingMapper {

    public static Booking mapToBooking(BookingDtoRequest bookingDtoRequest, User user, Item item) {
        Booking booking = new Booking();
        booking.setStatus(Status.WAITING);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStart(bookingDtoRequest.getStart());
        booking.setEnd(bookingDtoRequest.getEnd());
        return booking;
    }

    public static BookingDto mapToBookingDto(Booking booking) {
        BookingDto bookingDto = new BookingDto();
        bookingDto.setId(booking.getId());
        bookingDto.setStatus(booking.getStatus());
        bookingDto.setBooker(booking.getBooker());
        bookingDto.setItem(booking.getItem());
        bookingDto.setStart(booking.getStart());
        bookingDto.setEnd(booking.getEnd());
        return bookingDto;
    }

    public static List<BookingDto> mapToBookingDto(List<Booking> bookings) {
        List<BookingDto> bookingDtos = new ArrayList<>();
        for (Booking booking : bookings) {
            bookingDtos.add(mapToBookingDto(booking));
        }
        return bookingDtos;
    }
}

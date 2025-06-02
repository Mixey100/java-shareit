package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.enums.State;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private static final String USER_ID_HEADER = "X-Sharer-User-Id";
    private final BookingService bookingService;

    @PostMapping
    public BookingDto createBooking(@RequestBody BookingDtoRequest bookingDtoRequest,
                                    @RequestHeader(value = USER_ID_HEADER, required = false) Long bookerId) {
        return bookingService.createBooking(bookingDtoRequest, bookerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto approveBooking(@PathVariable Long bookingId,
                                     @RequestHeader(value = USER_ID_HEADER, required = false) Long ownerId,
                                     @RequestParam Boolean approved) {
        return bookingService.approveBooking(bookingId, approved, ownerId);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@PathVariable Long bookingId,
                                     @RequestHeader(value = USER_ID_HEADER, required = false) Long userId) {
        return bookingService.getBookingById(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingsByUserId(@RequestHeader(value = USER_ID_HEADER, required = false) Long bookerId,
                                                @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.getBookingsByUserId(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByUserItems(@RequestHeader(value = USER_ID_HEADER, required = false) Long ownerId,
                                                   @RequestParam(defaultValue = "ALL") State state) {
        return bookingService.getBookingsByItemsOwner(ownerId, state);
    }
}

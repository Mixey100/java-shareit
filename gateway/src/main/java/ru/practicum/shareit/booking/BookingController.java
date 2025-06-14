package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.State;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {

    private final BookingClient bookingClient;
    private static final String USER_ID_HEADER = "X-Sharer-User-Id";

    @PostMapping
    public ResponseEntity<Object> createBooking(@Valid @RequestBody BookingDtoRequest dto,
                                                @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingClient.createBooking(dto, userId);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> approveBooking(@RequestHeader(USER_ID_HEADER) Long userId, @PathVariable Long bookingId,
                                                 @RequestParam Boolean approved) {
        return bookingClient.approveBooking(userId, bookingId, approved);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@PathVariable Long bookingId,
                                                 @RequestHeader(USER_ID_HEADER) Long userId) {
        return bookingClient.getById(bookingId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingsByUserId(@RequestHeader(USER_ID_HEADER) Long userId,
                                                      @RequestParam(name = "state", defaultValue = "ALL") State state) {
        return bookingClient.getAllByUser(userId, state);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByUserItems(@RequestHeader(USER_ID_HEADER) Long userId,
                                                         @RequestParam(name = "state", defaultValue = "ALL") State state) {
        return bookingClient.getAllByUserItems(userId, state);
    }
}
package booking;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.model.enums.State;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(classes = ShareItServer.class)
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DbBookingServiceImplTest {

    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

    @Autowired
    BookingService bookingService;

    static UserDto userDtoRequest1;
    static UserDto userDtoRequest2;

    static ItemDtoRequest itemDtoRequest1;
    static ItemDtoRequest itemDtoRequest2;


    @BeforeAll
    static void init() {
        userDtoRequest1 = new UserDto(null, "user1", "user1@test.com");
        userDtoRequest2 = new UserDto(null, "user2", "user2@test.com");

        itemDtoRequest1 = new ItemDtoRequest("test1", "description1", true, null);
        itemDtoRequest2 = new ItemDtoRequest("test2", "description2", true, null);
    }

    @Test
    void testShouldCreateBooking() {
        UserDto userDtoResponse1 = userService.createUser(userDtoRequest1);
        UserDto userDtoResponse2 = userService.createUser(userDtoRequest2);
        ItemDto itemDtoResponse = itemService.createItem(itemDtoRequest1, userDtoResponse1.getId());
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));
        BookingDto bookingDtoResponse = bookingService.createBooking(bookingDtoRequest, userDtoResponse2.getId());

        Assertions.assertThat(bookingDtoResponse.getItem().getId()).isEqualTo(bookingDtoRequest.getItemId());
        Assertions.assertThat(bookingDtoResponse.getBooker().getId()).isEqualTo(userDtoResponse2.getId());
        Assertions.assertThat(bookingDtoResponse.getStart()).isEqualTo(bookingDtoRequest.getStart());
        Assertions.assertThat(bookingDtoResponse.getEnd()).isEqualTo(bookingDtoRequest.getEnd());
    }

    @Test
    void testShouldNotCreateBookingNoUser() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);
        ItemDto itemDtoResponse = itemService.createItem(itemDtoRequest1, userDtoResponse.getId());
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));

        Assertions.assertThatThrownBy(() ->
                bookingService.createBooking(bookingDtoRequest, 20L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testShouldNotCreateBookingNoItem() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(2L,
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));

        Assertions.assertThatThrownBy(() ->
                        bookingService.createBooking(bookingDtoRequest, userDtoResponse.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testShouldNotCreateBookingNotAvailableItem() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);
        ItemDtoRequest itemDtoNotAvailable =
                new ItemDtoRequest("test1", "description1", false, null);
        ItemDto itemDtoResponse = itemService.createItem(itemDtoNotAvailable, userDtoResponse.getId());
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));

        Assertions.assertThatThrownBy(() ->
                        bookingService.createBooking(bookingDtoRequest, userDtoResponse.getId()))
                .isInstanceOf(IllegalStateException.class);
    }

    @Test
    void testShouldApproveBooking() {
        UserDto userDtoResponse1 = userService.createUser(userDtoRequest1);
        UserDto userDtoResponse2 = userService.createUser(userDtoRequest2);
        ItemDto itemDtoResponse = itemService.createItem(itemDtoRequest1, userDtoResponse1.getId());
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));
        BookingDto bookingDtoResponse = bookingService.createBooking(bookingDtoRequest, userDtoResponse2.getId());

        BookingDto bookingApproveDto = bookingService.approveBooking(bookingDtoResponse.getId(),
                true, userDtoResponse1.getId());

        Assertions.assertThat(bookingApproveDto.getStatus()).isEqualTo(Status.APPROVED);
    }

    @Test
    void testShouldRejectBooking() {
        UserDto userDtoResponse1 = userService.createUser(userDtoRequest1);
        UserDto userDtoResponse2 = userService.createUser(userDtoRequest2);
        ItemDto itemDtoResponse = itemService.createItem(itemDtoRequest1, userDtoResponse1.getId());
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));
        BookingDto bookingDtoResponse = bookingService.createBooking(bookingDtoRequest, userDtoResponse2.getId());

        BookingDto bookingApproveDto = bookingService.approveBooking(bookingDtoResponse.getId(),
                false, userDtoResponse1.getId());

        Assertions.assertThat(bookingApproveDto.getStatus()).isEqualTo(Status.REJECTED);
    }

    @Test
    void testShouldNotApproveNoBooking() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);

        Assertions.assertThatThrownBy(() ->
                        bookingService.approveBooking(1L, true, userDtoResponse.getId()))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testShouldNotApproveNoOwner() {
        UserDto userDtoResponse1 = userService.createUser(userDtoRequest1);
        UserDto userDtoResponse2 = userService.createUser(userDtoRequest2);
        ItemDto itemDtoResponse = itemService.createItem(itemDtoRequest1, userDtoResponse1.getId());
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));
        BookingDto bookingDtoResponse = bookingService.createBooking(bookingDtoRequest, userDtoResponse2.getId());

        Assertions.assertThatThrownBy(() ->
                        bookingService.approveBooking(bookingDtoResponse.getId(), true, userDtoResponse2.getId()))
                .isInstanceOf(ValidationException.class);
    }

    @Test
    void testShouldGetBookingById() {
        UserDto userDtoResponse1 = userService.createUser(userDtoRequest1);
        UserDto userDtoResponse2 = userService.createUser(userDtoRequest2);
        ItemDto itemDtoResponse = itemService.createItem(itemDtoRequest1, userDtoResponse1.getId());
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));
        BookingDto bookingDtoResponse = bookingService.createBooking(bookingDtoRequest, userDtoResponse2.getId());

        BookingDto findBookingDto = bookingService.getBookingById(bookingDtoResponse.getId(), userDtoResponse2.getId());

        Assertions.assertThat(bookingDtoResponse.getItem().getId()).isEqualTo(findBookingDto.getItem().getId());
        Assertions.assertThat(bookingDtoResponse.getStart()).isEqualTo(findBookingDto.getStart());
        Assertions.assertThat(bookingDtoResponse.getEnd()).isEqualTo(findBookingDto.getEnd());
    }

    @Test
    void testShouldNotGetBookingByIdNoUser() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);
        ItemDto itemDtoResponse = itemService.createItem(itemDtoRequest1, userDtoResponse.getId());
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));
        BookingDto bookingDtoResponse = bookingService.createBooking(bookingDtoRequest, userDtoResponse.getId());

        Assertions.assertThatThrownBy(() ->
                bookingService.getBookingById(bookingDtoResponse.getId(), 2L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testShouldNotGetBookingByIdNoBooking() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);

        Assertions.assertThatThrownBy(() ->
                bookingService.getBookingById(2L, userDtoResponse.getId())).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testShouldGetAllCurrentBookings() {
        UserDto userDtoResponse1 = userService.createUser(userDtoRequest1);
        UserDto userDtoResponse2 = userService.createUser(userDtoRequest2);
        ItemDto itemDtoResponse = itemService.createItem(itemDtoRequest1, userDtoResponse1.getId());

        BookingDtoRequest bookingDtoRequest1 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().plusMinutes(10));
        BookingDtoRequest bookingDtoRequest2 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusHours(2));
        BookingDtoRequest bookingDtoRequest3 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(10), LocalDateTime.now().minusHours(9));

        bookingService.createBooking(bookingDtoRequest1, userDtoResponse2.getId());
        bookingService.createBooking(bookingDtoRequest2, userDtoResponse2.getId());
        bookingService.createBooking(bookingDtoRequest3, userDtoResponse2.getId());

        List<BookingDto> bookings = bookingService.getBookingsByUserId(userDtoResponse2.getId(), State.CURRENT);

        Assertions.assertThat(bookings.size()).isEqualTo(2);
    }

    @Test
    void testShouldGetAllPastBookings() {
        UserDto userDtoResponse1 = userService.createUser(userDtoRequest1);
        UserDto userDtoResponse2 = userService.createUser(userDtoRequest2);
        ItemDto itemDtoResponse = itemService.createItem(itemDtoRequest1, userDtoResponse1.getId());

        BookingDtoRequest bookingDtoRequest1 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(2), LocalDateTime.now().minusMinutes(110));
        BookingDtoRequest bookingDtoRequest2 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(50));
        BookingDtoRequest bookingDtoRequest3 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().plusHours(10), LocalDateTime.now().plusHours(11));

        bookingService.createBooking(bookingDtoRequest1, userDtoResponse2.getId());
        bookingService.createBooking(bookingDtoRequest2, userDtoResponse2.getId());
        bookingService.createBooking(bookingDtoRequest3, userDtoResponse2.getId());

        List<BookingDto> bookings = bookingService.getBookingsByUserId(userDtoResponse2.getId(), State.PAST);

        Assertions.assertThat(bookings.size()).isEqualTo(2);
    }

    @Test
    void testShouldGetAllFutureBookings() {
        UserDto userDtoResponse1 = userService.createUser(userDtoRequest1);
        UserDto userDtoResponse2 = userService.createUser(userDtoRequest2);
        ItemDto itemDtoResponse = itemService.createItem(itemDtoRequest1, userDtoResponse1.getId());

        BookingDtoRequest bookingDtoRequest1 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().plusHours(2), LocalDateTime.now().plusMinutes(115));
        BookingDtoRequest bookingDtoRequest2 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusMinutes(65));
        BookingDtoRequest bookingDtoRequest3 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(10), LocalDateTime.now().minusHours(9));

        bookingService.createBooking(bookingDtoRequest1, userDtoResponse2.getId());
        bookingService.createBooking(bookingDtoRequest2, userDtoResponse2.getId());
        bookingService.createBooking(bookingDtoRequest3, userDtoResponse2.getId());

        List<BookingDto> bookings = bookingService.getBookingsByUserId(userDtoResponse2.getId(), State.FUTURE);

        Assertions.assertThat(bookings.size()).isEqualTo(2);
    }

    @Test
    void testShouldGetAllBookings() {
        UserDto userDtoResponse1 = userService.createUser(userDtoRequest1);
        UserDto userDtoResponse2 = userService.createUser(userDtoRequest2);
        ItemDto itemDtoResponse = itemService.createItem(itemDtoRequest1, userDtoResponse1.getId());

        BookingDtoRequest bookingDtoRequest1 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().plusHours(2), LocalDateTime.now().plusMinutes(115));
        BookingDtoRequest bookingDtoRequest2 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusMinutes(65));
        BookingDtoRequest bookingDtoRequest3 = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().plusHours(3), LocalDateTime.now().plusMinutes(185));

        bookingService.createBooking(bookingDtoRequest1, userDtoResponse2.getId());
        bookingService.createBooking(bookingDtoRequest2, userDtoResponse2.getId());
        bookingService.createBooking(bookingDtoRequest3, userDtoResponse2.getId());

        List<BookingDto> bookings = bookingService.getBookingsByUserId(userDtoResponse2.getId(), State.ALL);

        Assertions.assertThat(bookings.size()).isEqualTo(3);
    }

    @Test
    void testShouldNotGetNoUser() {
        Assertions.assertThatThrownBy(() ->
                bookingService.getBookingsByUserId(1L, State.ALL)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testShouldGetAllByItems() {
        UserDto userDtoResponse1 = userService.createUser(userDtoRequest1);
        UserDto userDtoResponse2 = userService.createUser(userDtoRequest2);
        ItemDto itemDtoResponse1 = itemService.createItem(itemDtoRequest1, userDtoResponse1.getId());
        ItemDto itemDtoResponse2 = itemService.createItem(itemDtoRequest1, userDtoResponse1.getId());
        ItemDto itemDtoResponse3 = itemService.createItem(itemDtoRequest1, userDtoResponse1.getId());

        BookingDtoRequest bookingDtoRequest1 = new BookingDtoRequest(itemDtoResponse1.getId(),
                LocalDateTime.now().plusHours(2), LocalDateTime.now().plusMinutes(115));
        BookingDtoRequest bookingDtoRequest2 = new BookingDtoRequest(itemDtoResponse2.getId(),
                LocalDateTime.now().plusHours(1), LocalDateTime.now().plusMinutes(65));
        BookingDtoRequest bookingDtoRequest3 = new BookingDtoRequest(itemDtoResponse3.getId(),
                LocalDateTime.now().plusHours(3), LocalDateTime.now().plusMinutes(185));

        bookingService.createBooking(bookingDtoRequest1, userDtoResponse2.getId());
        bookingService.createBooking(bookingDtoRequest2, userDtoResponse2.getId());
        bookingService.createBooking(bookingDtoRequest3, userDtoResponse2.getId());

        List<BookingDto> bookings = bookingService.getBookingsByItemsOwner(userDtoResponse1.getId(), State.ALL);

        Assertions.assertThat(bookings.size()).isEqualTo(3);
    }
}

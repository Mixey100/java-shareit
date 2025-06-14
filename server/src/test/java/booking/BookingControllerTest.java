package booking;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.enums.Status;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class BookingControllerTest {

    private final ObjectMapper mapper = new ObjectMapper();

    @Mock
    BookingService bookingService;

    private MockMvc mvc;

    @InjectMocks
    private BookingController controller;

    private UserDto userDto;
    private BookingDto responseDto;
    private BookingDtoRequest requestDto;
    private Item item;
    private User user;
    private LocalDateTime now;

    @BeforeEach
    void setUp() {
        now = LocalDateTime.now();

        mvc = MockMvcBuilders
                .standaloneSetup(controller)
                .build();

        user = new User(1L, "email@mail.com", "name");
        item = new Item(1L, user, "name", "desc", true, null);
        userDto = new UserDto(1L, "test", "test@test.com");
        requestDto = new BookingDtoRequest(1L, null, null);
        responseDto = new BookingDto(1L, item, user, Status.WAITING, null, null);

    }

    @Test
    void testShouldCreateBooking() throws Exception {
        when(bookingService.createBooking(any(BookingDtoRequest.class), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(post("/bookings")
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item").value(item))
                .andExpect(jsonPath("$.booker").value(user))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void testShouldApproveBooking() throws Exception {
        when(bookingService.approveBooking(anyLong(), anyBoolean(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(patch("/bookings/" + responseDto.getId() + "/?approved=true")
                        .content(mapper.writeValueAsString(requestDto))
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item").value(item))
                .andExpect(jsonPath("$.booker").value(user))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void testShouldGetBookingById() throws Exception {
        when(bookingService.getBookingById(anyLong(), anyLong()))
                .thenReturn(responseDto);

        mvc.perform(get("/bookings/" + responseDto.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.item").value(item))
                .andExpect(jsonPath("$.booker").value(user))
                .andExpect(jsonPath("$.status").value("WAITING"));
    }

    @Test
    void testShouldGetBookingsByUserId() throws Exception {
        when(bookingService.getBookingsByUserId(anyLong(), any()))
                .thenReturn(List.of(responseDto));

        mvc.perform(get("/bookings?state=")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect((result -> {
                    String json = result.getResponse().getContentAsString();
                    List<BookingDto> dtos = mapper.readValue(json, new TypeReference<>() {
                    });
                    if (dtos.isEmpty()) {
                        throw new AssertionError("Empty ItemDtoResponse list");
                    }
                }));
    }

    @Test
    void testShouldGetBookingsByItemsOwner() throws Exception {
        when(bookingService.getBookingsByItemsOwner(anyLong(), any()))
                .thenReturn(List.of(responseDto));

        mvc.perform(get("/bookings/owner?state=")
                        .contentType(MediaType.APPLICATION_JSON)
                        .header("X-Sharer-User-Id", userDto.getId()))
                .andExpect(status().isOk())
                .andExpect((result -> {
                    String json = result.getResponse().getContentAsString();
                    List<BookingDto> dtos = mapper.readValue(json, new TypeReference<>() {
                    });
                    if (dtos.isEmpty()) {
                        throw new AssertionError("Empty ItemDtoResponse list");
                    }
                }));
    }
}

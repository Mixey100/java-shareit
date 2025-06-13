package item;

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
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.dto.CommentDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest(classes = ShareItServer.class)
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DbItemServiceImplTest {

    @Autowired
    ItemService itemService;

    @Autowired
    UserService userService;

    @Autowired
    BookingService bookingService;

    @Autowired
    ItemRequestService itemRequestService;

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
    void testShouldCreateItem() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);
        ItemDto itemDtoResponse = itemService.createItem(itemDtoRequest1, userDtoResponse.getId());

        Assertions.assertThat(itemDtoResponse.getName()).isEqualTo(itemDtoRequest1.getName());
        Assertions.assertThat(itemDtoResponse.getDescription()).isEqualTo(itemDtoRequest1.getDescription());
        Assertions.assertThat(itemDtoResponse.getAvailable()).isEqualTo(itemDtoRequest1.getAvailable());
    }

    @Test
    void testShouldCreateItemWithRequest() {
        UserDto userDtoResponse1 = userService.createUser(userDtoRequest1);
        UserDto userDtoResponse2 = userService.createUser(userDtoRequest2);

        ItemRequestDtoRequest itemRequestDtoRequest = new ItemRequestDtoRequest("request description");
        ItemRequestDto itemRequestDtoResponse =
                itemRequestService.createRequest(itemRequestDtoRequest, userDtoResponse2.getId());

        ItemDtoRequest itemDtoRequest = new ItemDtoRequest("name", "desc",
                true, itemRequestDtoResponse.getId());
        ItemDto itemDtoResponse = itemService.createItem(itemDtoRequest, userDtoResponse1.getId());

        Assertions.assertThat(itemDtoResponse.getName()).isEqualTo(itemDtoRequest.getName());
        Assertions.assertThat(itemDtoResponse.getDescription()).isEqualTo(itemDtoRequest.getDescription());
        Assertions.assertThat(itemDtoResponse.getAvailable()).isEqualTo(itemDtoRequest.getAvailable());
    }

    @Test
    void testShouldNotCreateItemNotUser() {
        Assertions.assertThatThrownBy(() -> {
            itemService.createItem(itemDtoRequest1, 1L);
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testShouldUpdateItem() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);
        ItemDto itemDtoCreateResponse = itemService.createItem(itemDtoRequest1, userDtoResponse.getId());
        ItemDto itemDtoUpdateResponse = itemService.updateItem(itemDtoRequest2,
                itemDtoCreateResponse.getId(), userDtoResponse.getId());

        Assertions.assertThat(itemDtoUpdateResponse.getName()).isEqualTo(itemDtoRequest2.getName());
        Assertions.assertThat(itemDtoUpdateResponse.getDescription()).isEqualTo(itemDtoRequest2.getDescription());
        Assertions.assertThat(itemDtoUpdateResponse.getAvailable()).isEqualTo(itemDtoRequest2.getAvailable());
    }

    @Test
    void testShouldNotUpdateNoItem() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);

        Assertions.assertThatThrownBy(() -> {
            itemService.updateItem(itemDtoRequest2, 1L, userDtoResponse.getId());
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testShouldNotUpdateWrongUser() {
        UserDto userDtoResponse1 = userService.createUser(userDtoRequest1);
        UserDto userDtoResponse2 = userService.createUser(userDtoRequest2);

        ItemDto itemDtoResponse = itemService.createItem(itemDtoRequest1, userDtoResponse1.getId());

        Assertions.assertThatThrownBy(() -> {
            itemService.updateItem(itemDtoRequest2, itemDtoResponse.getId(), userDtoResponse2.getId());
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testShouldUpdateOnlyName() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);
        ItemDtoRequest dtoName = new ItemDtoRequest("new_name", null, null, null);
        ItemDto itemDtoCreateResponse = itemService.createItem(itemDtoRequest1, userDtoResponse.getId());
        ItemDto itemDtoUpdateResponse = itemService.updateItem(dtoName,
                itemDtoCreateResponse.getId(), userDtoResponse.getId());

        Assertions.assertThat(itemDtoUpdateResponse.getName()).isEqualTo(dtoName.getName());
        Assertions.assertThat(itemDtoUpdateResponse.getDescription()).isEqualTo(itemDtoRequest1.getDescription());
        Assertions.assertThat(itemDtoUpdateResponse.getAvailable()).isEqualTo(itemDtoRequest1.getAvailable());
    }

    @Test
    void testShouldUpdateOnlyDescription() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);
        ItemDtoRequest dtoDescription = new ItemDtoRequest(null, "new description", null, null);
        ItemDto itemDtoCreateResponse = itemService.createItem(itemDtoRequest1, userDtoResponse.getId());
        ItemDto itemDtoUpdateResponse = itemService.updateItem(dtoDescription,
                itemDtoCreateResponse.getId(), userDtoResponse.getId());

        Assertions.assertThat(itemDtoUpdateResponse.getDescription()).isEqualTo(dtoDescription.getDescription());
        Assertions.assertThat(itemDtoUpdateResponse.getName()).isEqualTo(itemDtoRequest1.getName());
        Assertions.assertThat(itemDtoUpdateResponse.getAvailable()).isEqualTo(itemDtoRequest1.getAvailable());
    }

    @Test
    void testShouldUpdateOnlyAvailable() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);
        ItemDtoRequest dtoAvailable = new ItemDtoRequest(null, null, false, null);
        ItemDto itemDtoCreateResponse = itemService.createItem(itemDtoRequest1, userDtoResponse.getId());
        ItemDto itemDtoUpdateResponse = itemService.updateItem(dtoAvailable,
                itemDtoCreateResponse.getId(), userDtoResponse.getId());

        Assertions.assertThat(itemDtoUpdateResponse.getAvailable()).isEqualTo(dtoAvailable.getAvailable());
        Assertions.assertThat(itemDtoUpdateResponse.getDescription()).isEqualTo(itemDtoRequest1.getDescription());
        Assertions.assertThat(itemDtoUpdateResponse.getName()).isEqualTo(itemDtoRequest1.getName());
    }

    @Test
    void testShouldFindItemById() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);
        ItemDto itemDtoResponseCreate = itemService.createItem(itemDtoRequest1, userDtoResponse.getId());
        ItemDto itemDtoResponseFind = itemService.getItemById(itemDtoResponseCreate.getId());

        Assertions.assertThat(itemDtoResponseCreate.getName()).isEqualTo(itemDtoResponseFind.getName());
        Assertions.assertThat(itemDtoResponseCreate.getDescription()).isEqualTo(itemDtoResponseFind.getDescription());
        Assertions.assertThat(itemDtoResponseCreate.getAvailable()).isEqualTo(itemDtoResponseFind.getAvailable());
    }

    @Test
    void testShouldNotFindItemById() {
        Assertions.assertThatThrownBy(() -> {
            itemService.getItemById(1L);
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testShouldFindItemByOwnerId() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);
        ItemDto itemDtoResponseCreate = itemService.createItem(itemDtoRequest1, userDtoResponse.getId());
        List<ItemDto> itemDtoResponseFind = itemService.getItemsByOwnerId(userDtoResponse.getId());

        Assertions.assertThat(itemDtoResponseFind.size()).isEqualTo(1);
    }

    @Test
    void testShouldFindItemByText() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);
        ItemDto itemDtoResponseCreate1 = itemService.createItem(itemDtoRequest1, userDtoResponse.getId());
        ItemDto itemDtoResponseCreate2 = itemService.createItem(itemDtoRequest2, userDtoResponse.getId());
        List<ItemDto> itemDtoResponseName = itemService.getItemByText(itemDtoRequest1.getName());
        List<ItemDto> itemDtoResponseDesc = itemService.getItemByText(itemDtoRequest2.getDescription());

        Assertions.assertThat(itemDtoResponseName.size()).isEqualTo(1);
        Assertions.assertThat(itemDtoResponseDesc.size()).isEqualTo(1);
    }

    @Test
    void testShouldDeleteItem() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);
        ItemDto itemDtoResponse = itemService.createItem(itemDtoRequest1, userDtoResponse.getId());
        ItemDto itemDtoFind = itemService.getItemById(itemDtoResponse.getId());

        Assertions.assertThat(itemDtoResponse.getId()).isEqualTo(itemDtoFind.getId());

        itemService.deleteItem(itemDtoResponse.getId());

        Assertions.assertThatThrownBy(() -> {
            itemService.getItemById(itemDtoResponse.getId());
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testShouldAddCommentToItem() {
        UserDto ownerDtoResponse = userService.createUser(userDtoRequest1);
        ItemDto itemDtoResponse = itemService.createItem(itemDtoRequest1, ownerDtoResponse.getId());

        UserDto bookerDtoResponse = userService.createUser(userDtoRequest2);
        BookingDtoRequest bookingDtoRequest = new BookingDtoRequest(itemDtoResponse.getId(),
                LocalDateTime.now().minusHours(1), LocalDateTime.now().minusMinutes(30));
        BookingDto bookingDtoResponse = bookingService.createBooking(bookingDtoRequest, bookerDtoResponse.getId());
        bookingService.approveBooking(bookingDtoResponse.getId(), true, ownerDtoResponse.getId());

        CommentDtoRequest commentDtoRequest = new CommentDtoRequest("comment");

        itemService.addComment(commentDtoRequest, itemDtoResponse.getId(), bookerDtoResponse.getId());
        ItemDto itemWithComment = itemService.getItemById(itemDtoResponse.getId());

        Assertions.assertThat(itemWithComment.getComments().size()).isEqualTo(1);
    }

    @Test
    void testShouldNotAddCommentNoBooking() {
        UserDto ownerDtoResponse = userService.createUser(userDtoRequest1);
        ItemDto itemDtoResponse = itemService.createItem(itemDtoRequest1, ownerDtoResponse.getId());

        CommentDtoRequest commentDtoRequest = new CommentDtoRequest("comment");

        Assertions.assertThatThrownBy(() -> {
            itemService.addComment(commentDtoRequest, itemDtoResponse.getId(), ownerDtoResponse.getId());
        }).isInstanceOf(ValidationException.class);
    }
}

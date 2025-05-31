package request;

import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.ShareItServer;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@SpringBootTest(classes = ShareItServer.class)
@ActiveProfiles("test")
@Transactional
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DbItemRequestServiceImplTest {

    @Autowired
    UserService userService;

    @Autowired
    ItemRequestService itemRequestService;

    static UserDto userDtoRequest1;
    static UserDto userDtoRequest2;
    static ItemRequestDtoRequest itemRequestDtoRequest;

    @BeforeAll
    static void init() {
        userDtoRequest1 = new UserDto(null, "user1", "user1@test.com");
        userDtoRequest2 = new UserDto(null, "user2", "user2@test.com");
        itemRequestDtoRequest = new ItemRequestDtoRequest("request description");
    }

    @Test
    void testShouldCreateItemRequest() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);
        ItemRequestDto itemRequestDtoResponse = itemRequestService
                .createRequest(itemRequestDtoRequest, userDtoResponse.getId());

        Assertions.assertThat(itemRequestDtoResponse.getDescription()).isEqualTo(itemRequestDtoRequest.getDescription());
    }

    @Test
    void testShouldNotCreateNotUser() {
        Assertions.assertThatThrownBy(() ->
                itemRequestService.createRequest(itemRequestDtoRequest, 1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testShouldGetUserRequests() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);
        ItemRequestDto request1 = itemRequestService.createRequest(itemRequestDtoRequest, userDtoResponse.getId());
        ItemRequestDto request2 = itemRequestService.createRequest(itemRequestDtoRequest, userDtoResponse.getId());
        ItemRequestDto request3 = itemRequestService.createRequest(itemRequestDtoRequest, userDtoResponse.getId());

        List<ItemRequestDto> requests = itemRequestService.getRequestsByRequestorId(userDtoResponse.getId());

        Assertions.assertThat(requests.size()).isEqualTo(3);
        Assertions.assertThat(requests.get(0).getId()).isEqualTo(request3.getId());
        Assertions.assertThat(requests.get(1).getId()).isEqualTo(request2.getId());
        Assertions.assertThat(requests.get(2).getId()).isEqualTo(request1.getId());
    }

    @Test
    void testShouldNotGeNotUser() {
        Assertions.assertThatThrownBy(() ->
                itemRequestService.getRequestsByRequestorId(1L)).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testShouldGetAllRequestsExceptOwn() {
        UserDto userDtoResponse1 = userService.createUser(userDtoRequest1);
        UserDto userDtoResponse2 = userService.createUser(userDtoRequest2);
        itemRequestService.createRequest(itemRequestDtoRequest, userDtoResponse1.getId());
        itemRequestService.createRequest(itemRequestDtoRequest, userDtoResponse1.getId());
        itemRequestService.createRequest(itemRequestDtoRequest, userDtoResponse2.getId());
        itemRequestService.createRequest(itemRequestDtoRequest, userDtoResponse2.getId());

        List<ItemRequestDto> requests = itemRequestService.getAllRequest(userDtoResponse2.getId());

        Assertions.assertThat(requests.size()).isEqualTo(2);
    }

    @Test
    void testShouldGetRequestById() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);
        ItemRequestDto itemRequestDtoResponse = itemRequestService
                .createRequest(itemRequestDtoRequest, userDtoResponse.getId());

        ItemRequestDto findItemResponse = itemRequestService
                .getRequestById(itemRequestDtoResponse.getId(), userDtoResponse.getId());

        Assertions.assertThat(findItemResponse.getDescription()).isEqualTo(itemRequestDtoRequest.getDescription());
    }

    @Test
    void testShouldNotGetRequestByIdNoUser() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);
        ItemRequestDto itemRequestDtoResponse = itemRequestService
                .createRequest(itemRequestDtoRequest, userDtoResponse.getId());

        Assertions.assertThatThrownBy(() ->
                        itemRequestService.getRequestById(itemRequestDtoResponse.getId(), 2L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void testShouldNotGetRequestByIdNoRequest() {
        UserDto userDtoResponse = userService.createUser(userDtoRequest1);

        Assertions.assertThatThrownBy(() ->
                        itemRequestService.getRequestById(2L, userDtoResponse.getId()))
                .isInstanceOf(NotFoundException.class);
    }

}
package user;

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
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

@SpringBootTest(classes = ShareItServer.class)
@ActiveProfiles("test")
@Transactional
@FieldDefaults(level = AccessLevel.PRIVATE)
public class DbUserServiceImplTest {

    @Autowired
    UserService userService;

    static UserDto userDtoRequest1;
    static UserDto userDtoRequest2;

    @BeforeAll
    static void initUsers() {
        userDtoRequest1 = new UserDto(null, "user1", "user1@test.com");
        userDtoRequest2 = new UserDto(null, "user2", "user2@test.com");
    }

    @Test
    void testShouldCreateUser() {
        UserDto userDtoResponse1 = userService.createUser(userDtoRequest1);

        Assertions.assertThat(userDtoResponse1.getEmail()).isEqualTo(userDtoRequest1.getEmail());
        Assertions.assertThat(userDtoResponse1.getName()).isEqualTo(userDtoRequest1.getName());
    }

    @Test
    void testShouldUpdateUser() {
        UserDto userDtoResponse1 = userService.createUser(userDtoRequest1);
        UserDto userDtoResponse2 = userService.updateUser(userDtoRequest2, userDtoResponse1.getId());

        Assertions.assertThat(userDtoResponse2.getEmail()).isEqualTo(userDtoRequest2.getEmail());
        Assertions.assertThat(userDtoResponse2.getName()).isEqualTo(userDtoRequest2.getName());
    }

    @Test
    void testShouldNotUpdateUserEmailConflict() {
        userService.createUser(userDtoRequest1);
        UserDto userDtoResponse2 = userService.createUser(userDtoRequest2);

        UserDto updateDto = new UserDto(null, "test", userDtoRequest1.getEmail());

        Assertions.assertThatThrownBy(() -> {
            userService.updateUser(updateDto, userDtoResponse2.getId());
        }).isInstanceOf(ConflictException.class);
    }

    @Test
    void testShouldNotUpdateNotUser() {
        UserDto updateDto = new UserDto(null, "test", userDtoRequest1.getEmail());

        Assertions.assertThatThrownBy(() -> {
            userService.updateUser(updateDto, 1L);
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testShouldNotUpdateNameIfNullName() {
        UserDto userDtoResponse1 = userService.createUser(userDtoRequest1);
        UserDto userDtoRequestNullName = new UserDto(null, null, "user2@test.com");
        UserDto userDtoResponse2 = userService.updateUser(userDtoRequestNullName, userDtoResponse1.getId());

        Assertions.assertThat(userDtoResponse2.getEmail()).isEqualTo(userDtoRequest2.getEmail());
        Assertions.assertThat(userDtoResponse2.getName()).isEqualTo(userDtoRequest1.getName());
    }

    @Test
    void testShouldNotUpdateNameIfNullEmail() {
        UserDto userDtoResponse1 = userService.createUser(userDtoRequest1);
        UserDto userDtoRequestNullName = new UserDto(null, "user2", null);
        UserDto userDtoResponse2 = userService.updateUser(userDtoRequestNullName, userDtoResponse1.getId());

        Assertions.assertThat(userDtoResponse2.getEmail()).isEqualTo(userDtoRequest1.getEmail());
        Assertions.assertThat(userDtoResponse2.getName()).isEqualTo(userDtoRequest2.getName());
    }

    @Test
    void testShouldDeleteUser() {
        UserDto userDtoResponse1 = userService.createUser(userDtoRequest1);
        UserDto findUserDto = userService.getUserById(userDtoResponse1.getId());

        Assertions.assertThat(userDtoResponse1.getId()).isEqualTo(findUserDto.getId());

        userService.deleteUser(userDtoResponse1.getId());

        Assertions.assertThatThrownBy(() -> {
            userService.getUserById(userDtoResponse1.getId());
        }).isInstanceOf(NotFoundException.class);
    }

    @Test
    void testShouldGetUserById() {
        UserDto userDtoResponse1 = userService.createUser(userDtoRequest1);
        UserDto findUserResponse = userService.getUserById(userDtoResponse1.getId());

        Assertions.assertThat(userDtoResponse1.getId()).isEqualTo(findUserResponse.getId());
        Assertions.assertThat(userDtoResponse1.getEmail()).isEqualTo(findUserResponse.getEmail());
        Assertions.assertThat(userDtoResponse1.getName()).isEqualTo(findUserResponse.getName());
    }

    @Test
    void testShouldGetAllUsers() {
        userService.createUser(userDtoRequest1);
        userService.createUser(userDtoRequest2);
        List<UserDto> usersList = userService.getUsers();

        Assertions.assertThat(usersList.size()).isEqualTo(2);
    }

}

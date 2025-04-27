package ru.practicum.shareit.user.service;

import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.List;

public interface UserService {

    List<UserDto> getUsers();

    UserDto getUserById(Long id);

    UserDto createUser(User user);

    UserDto updateUser(User user);

    void deleteUser(Long id);


}

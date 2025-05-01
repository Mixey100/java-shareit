package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserStorage storage;

    @Override
    public List<UserDto> getUsers() {
        return storage.getUsers()
                .stream()
                .map(UserMapper::mapToUserDto)
                .toList();
    }

    @Override
    public UserDto getUserById(Long id) {
        return storage.getUserById(id)
                .map(UserMapper::mapToUserDto)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
    }

    @Override
    public UserDto createUser(User user) {
        user = storage.createUser(user);
        UserDto userDto = UserMapper.mapToUserDto(user);
        log.info("Пользователь c id {} {} добавлен c email {}", userDto.getId(), userDto.getName(), userDto.getEmail());
        return userDto;
    }

    @Override
    public UserDto updateUser(User user, Long id) {
        User oldUser = storage.getUserById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        if (user.getEmail() != null) {
            oldUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        log.info("Пользователь c id {} обновлен на имя {} и email {}", oldUser.getId(), oldUser.getName(), oldUser.getEmail());
        return UserMapper.mapToUserDto(storage.updateUser(oldUser));
    }

    @Override
    public void deleteUser(Long id) {
        getUserById(id);
        storage.deleteUser(id);
    }
}


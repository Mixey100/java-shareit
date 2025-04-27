package ru.practicum.shareit.user.service;

import lombok.extern.slf4j.Slf4j;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Service
public class InMemoryUserService implements UserService {

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
        checkName(user);
        checkEmail(user);
        log.info("Пользователь {} добавлен", user.getName());
        return UserMapper.mapToUserDto(storage.createUser(user));
    }

    @Override
    public UserDto updateUser(User user) {
        if (user.getId() == null) {
            throw new ValidationException("Должен быть указан id пользователя");
        }
        UserDto oldUser = getUserById(user.getId());
        if (user.getEmail() != null) {
            checkEmail(user);
            oldUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            oldUser.setName(user.getName());
        }
        return oldUser;
    }

    @Override
    public void deleteUser(Long id) {
        getUserById(id);
        storage.deleteUser(id);
    }

    private void checkEmail(User user) {
        if (user.getEmail() == null || user.getEmail().isBlank()) {
            log.error("Имейл не введен");
            throw new ValidationException("Должен быть указан имейл");
        }
        if (!user.getEmail().contains("@")) {
            log.error("Имейл не содержит символ @");
            throw new ValidationException("В имейле должен содержаться символ @");
        }
    }

    private void checkName(User user) {
        if (user.getName() == null || user.getName().isBlank()) {
            log.error("Имя пользователя не указано");
            throw new ValidationException("Должно быть указано имя пользователя");
        }
    }
}


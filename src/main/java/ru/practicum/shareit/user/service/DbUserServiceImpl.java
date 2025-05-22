package ru.practicum.shareit.user.service;


import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.storage.UserRepository;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DbUserServiceImpl implements UserService {

    private final UserRepository repository;

    @Override
    public List<UserDto> getUsers() {
        List<User> users = repository.findAll();
        return UserMapper.mapToUserDto(users);
    }

    @Override
    public UserDto getUserById(Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto createUser(UserDto userDto) {
        User entity = UserMapper.mapToUser(userDto);
        User user = repository.save(entity);
        return UserMapper.mapToUserDto(user);
    }

    @Override
    @Transactional
    public UserDto updateUser(UserDto userDto, Long id) {
        User user = repository.findById(id)
                .orElseThrow(() -> new NotFoundException("Пользователь с id " + id + " не найден"));
        if (userDto.getName() != null && !userDto.getName().isBlank()) {
            user.setName(userDto.getName());
        }
        if (userDto.getEmail() != null && !userDto.getEmail().isBlank()) {
            if (repository.findByEmail(userDto.getEmail()).isEmpty() || user.getEmail().equals(userDto.getEmail())) {
                user.setEmail(userDto.getEmail());
            } else {
                log.error("Email уже существует");
                throw new ConflictException("Email уже существует");
            }
        }
        return UserMapper.mapToUserDto(repository.save(user));
    }

    @Override
    @Transactional
    public void deleteUser(Long id) {
        User user = repository.getReferenceById(id);
        repository.delete(user);
    }
}

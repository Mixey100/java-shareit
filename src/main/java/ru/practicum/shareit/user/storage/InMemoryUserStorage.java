package ru.practicum.shareit.user.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.ConflictException;
import ru.practicum.shareit.user.User;

import java.util.*;

@Slf4j
@Component
public class InMemoryUserStorage implements UserStorage {

    Map<Long, User> users = new HashMap<>();

    @Override
    public List<User> getUsers() {
        return new ArrayList<>(users.values());
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    @Override
    public User createUser(User user) {
        user.setId(getId());
        checkExistEmail(user);
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User updateUser(User user) {
        if (user.getEmail() != null || !user.getEmail().isBlank()) {
            checkExistEmail(user);
        }
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public void deleteUser(Long id) {
        users.remove(id);
    }

    private void checkExistEmail(User user) {
        users.values().forEach(mapUser -> {
            if (mapUser.getEmail().equals(user.getEmail()) && !Objects.equals(mapUser.getId(), user.getId())) {
                log.error("Повтор email");
                throw new ConflictException("Пользователь с данным email уже существует");
            }
        });
    }

    private long getId() {
        long currentMaxId = users.keySet().stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

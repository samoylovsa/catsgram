package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.exception.NotFoundException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> findAll() {
        return users.values();
    }

    public Collection<User> findById(Long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Не найден пользователь с id: " + id);
        }
        return Collections.singletonList(users.get(id));
    }

    public User create(User user) {
        if (user.getEmail() == null) {
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }

        users.values().stream()
                .filter(u -> u.getEmail().equals(user.getEmail()))
                .findFirst()
                .ifPresent(u -> {
                    throw new DuplicatedDataException("Этот имейл уже используется");
                });

        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());
        users.put(user.getId(), user);

        return user;
    }

    public User update(User user) {
        if (user.getId() == null) {
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        if (!users.containsKey(user.getId())) {
            throw new ConditionsNotMetException("Пользователь с указанным id не найден");
        }

        User existingUser = users.get(user.getId());
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail())) {
            users.values().stream()
                    .filter(u -> u.getEmail().equals(user.getEmail()))
                    .findFirst()
                    .ifPresent(u -> {
                        throw new DuplicatedDataException("Этот имейл уже используется");
                    });
        }
        if (user.getUsername() != null) {
            existingUser.setUsername(user.getUsername());
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getPassword() != null) {
            existingUser.setPassword(user.getPassword());
        }

        return existingUser;
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}

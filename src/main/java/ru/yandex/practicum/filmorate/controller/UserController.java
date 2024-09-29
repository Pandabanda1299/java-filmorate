package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private final HashMap<Long, User> users = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(UserController.class);
    private static Long id = 0L;

    @GetMapping
    public List<User> getUsers() {
        LOG.info("Get all users");
        return new ArrayList<>(users.values());
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        validateUser(user);
        user.setId(++id);
        users.put(user.getId(), user);
        LOG.info("User created: {}", user);
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (user.getId() == null) {
            LOG.error("Идентификатор пользователя должен быть предоставлен для обновления");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        User existingUser = users.get(user.getId());
        if (existingUser == null) {
            LOG.error("Пользователь с идентификатором  не найден", user.getId());
            throw new ConditionsNotMetException("Пользователь не найден");
        }
        validateUser(user);
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail()) &&
                users.values().stream().anyMatch(existing -> existing.getEmail().equals(user.getEmail()))) {
            LOG.error("Адрес электронной почты уже используется", user.getEmail());
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        LOG.info("Пользователь с идентификатором  обновлен.", user.getId());
        return existingUser;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            LOG.error("Неверный адрес электронной почты: ", user.getEmail());
            throw new ValidationException("Электронная почта не может быть пустой");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            LOG.error("Неверный логин: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            LOG.error("Неверный день рождения: ", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
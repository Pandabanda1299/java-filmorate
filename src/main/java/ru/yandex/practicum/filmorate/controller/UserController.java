package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ConditionsNotMetException;
import ru.yandex.practicum.filmorate.exception.DuplicatedDataException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
public class UserController {
    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private final Map<Long, User> users = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong();

    @GetMapping
    public List<User> getUsers() {
        return users.values().stream().collect(Collectors.toList());
    }

    @PostMapping
    public User addUser(@RequestBody User user) {
        validateUser(user);
        if (users.values().stream().anyMatch(existingUser -> existingUser.getEmail().equals(user.getEmail()))) {
            log.error("Адрес электронной почты {} уже используется", user.getEmail());
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        user.setId(idCounter.incrementAndGet());
        users.put(user.getId(), user);
        log.info("Добавлен пользователь с идентификатором {}", user.getId());
        return user;
    }

    @PutMapping
    public User updateUser(@RequestBody User user) {
        if (user.getId() == null) {
            log.error("Идентификатор пользователя должен быть предоставлен для обновления");
            throw new ConditionsNotMetException("Id должен быть указан");
        }
        User existingUser = users.get(user.getId());
        if (existingUser == null) {
            log.error("Пользователь с идентификатором {} не найден", user.getId());
            throw new ConditionsNotMetException("Пользователь не найден");
        }
        validateUser(user);
        if (user.getEmail() != null && !user.getEmail().equals(existingUser.getEmail()) &&
                users.values().stream().anyMatch(existing -> existing.getEmail().equals(user.getEmail()))) {
            log.error("Адрес электронной почты {} уже используется", user.getEmail());
            throw new DuplicatedDataException("Этот имейл уже используется");
        }
        if (user.getEmail() != null) {
            existingUser.setEmail(user.getEmail());
        }
        if (user.getName() != null) {
            existingUser.setName(user.getName());
        }
        if (user.getPassword() != null) {
            existingUser.setPassword(user.getPassword());
        }
        log.info("Пользователь с идентификатором {} обновлен.", user.getId());
        return existingUser;
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.error("Неверный адрес электронной почты: {}", user.getEmail());
            throw new ValidationException("Электронная почта не может быть пустой и должна содержать символ @");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Неверный логин: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым и содержать пробелы");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Неверный день рождения: {}", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
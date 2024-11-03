package ru.yandex.practicum.filmorate.storage.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {

    private long id = 0L;
    private final HashMap<Long, User> users = new HashMap<>();

    public List<User> getFriends(long userId) {
        return users.get(userId).getFriends().stream()
                .map(this::getUserById)
                .toList();
    }

    @Override
    public User getUserById(long id) {
        if (!users.containsKey(id)) {
            throw new NotFoundException("Юзер с таким id не найден  " + id);
        }
        log.info("Юзер по id");
        return users.get(id);
    }

    @Override
    public List<User> getUsers() {
        log.info("Получить всех юзеров");
        return new ArrayList<>(users.values());
    }

    @Override
    public User addUser(@RequestBody User user) {
        user.setId(++id);
        user.setFriends(new HashSet<>());
        users.put(user.getId(), user);
        log.info("Пользователь создан: {}", user);
        return user;
    }

    public User updateUser(@RequestBody User user) {
        if (user.getId() != 0) {
            if (users.containsKey(user.getId())) {
                users.put(user.getId(), user);
                log.info("Пользователь обновлен: {}", user);
                return user;
            }
        }
        log.warn("User id is invalid");
        throw new NotFoundException("Пользователь с id " + user.getId() + " не найден");
    }
}
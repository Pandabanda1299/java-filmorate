package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.time.LocalDate;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor

public class UserService {
    private final InMemoryUserStorage userStorage;

    public User createUser(User user) {
        validateUser(user);
        return userStorage.addUser(user);
    }

    public User updateUser(User user) {
        validateUser(user);
        return userStorage.updateUser(user);
    }

    public User addFriend(long userId, long friendId) {
        checkUserAndFriend(userId, friendId);
        User friend = userStorage.getUserById(friendId);
        User user = userStorage.getUserById(userId);
        user.getFriends().add(friendId);
        friend.getFriends().add(userId);
        return user;
    }

    public User removeFriend(long userId, long friendId) {
        checkUserAndFriend(userId, friendId);
        User friend = userStorage.getUserById(friendId);
        User user = userStorage.getUserById(userId);
        user.getFriends().remove(friendId);
        friend.getFriends().remove(userId);
        return user;
    }

    public List<User> commonFriend(long userId, long friendId) {
        checkUserAndFriend(userId, friendId);
        User friend = userStorage.getUserById(friendId);
        User user = userStorage.getUserById(userId);
        return user.getFriends()
                .stream().filter(f -> friend.getFriends().contains(f))
                .map(userStorage::getUserById)
                .toList();

    }

    public List<User> getFriends(long userId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Юзер с таким id не найден " + userId);
        }
        return userStorage.getFriends(userId);
    }


    private void checkUserAndFriend(long userId, long friendId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Юзер с таким id не найден " + userId);
        }
        if (userStorage.getUserById(friendId) == null) {
            throw new NotFoundException("Друга с таким id не существует " + friendId);
        }
    }

    private void validateUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty() || !user.getEmail().contains("@")) {
            log.error("Неверный адрес электронной почты: ", user.getEmail());
            throw new ValidationException("Электронная почта не может быть пустой");
        }
        if (user.getLogin() == null || user.getLogin().isEmpty() || user.getLogin().contains(" ")) {
            log.error("Неверный логин: {}", user.getLogin());
            throw new ValidationException("Логин не может быть пустым");
        }
        if (user.getName() == null || user.getName().isEmpty()) {
            user.setName(user.getLogin());
        }
        if (user.getBirthday().isAfter(LocalDate.now())) {
            log.error("Неверный день рождения: ", user.getBirthday());
            throw new ValidationException("Дата рождения не может быть в будущем");
        }
    }
}
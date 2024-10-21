package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.List;

//Добавление и удаление друзей

@Slf4j
@Service
@RequiredArgsConstructor

public class UserService {
    private final InMemoryUserStorage userStorage;

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


    private void checkUserAndFriend(long userId, long friendId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Юзер с таким id не найден " + userId);
        }
        if (userStorage.getUserById(friendId) == null) {
            throw new NotFoundException("Друга с таким id не существует " + friendId);
        }
    }
}
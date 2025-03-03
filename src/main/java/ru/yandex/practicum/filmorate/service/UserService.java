package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FriendsRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;
import java.util.List;


@Slf4j
@Service
@RequiredArgsConstructor

public class UserService {
    private final UserRepository userRepository;
    private final FriendsRepository friendsRepository;

    public User createUser(User user) {
        validateUser(user);
        return userRepository.create(user);
    }

    public User updateUser(User user) {
        validateUser(user);
        return userRepository.update(user);
    }

    public User addFriend(long userId, long friendId) {
        checkUserAndFriend(userId, friendId);
        return friendsRepository.addFriend(userId, friendId);
    }

    public User removeFriend(long userId, long friendId) {
        checkUserAndFriend(userId, friendId);
        return friendsRepository.deleteFriend(userId, friendId);
    }

    public List<User> commonFriend(long userId, long friendId) {
        checkUserAndFriend(userId, friendId);
        return friendsRepository.getCommonFriends(userId, friendId);

    }

    public List<User> getFriends(long userId) {
        if (userRepository.findById(userId) == null) {
            throw new NotFoundException("Юзер с таким id не найден " + userId);
        }
        return friendsRepository.getFriends(userId);
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public User findById(long id) {
        return userRepository.findById(id);
    }


    private void checkUserAndFriend(long userId, long friendId) {
        if (userRepository.findById(userId) == null) {
            throw new NotFoundException("Юзер с таким id не найден " + userId);
        }
        if (userRepository.findById(friendId) == null) {
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
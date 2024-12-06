package ru.yandex.practicum.filmorate.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.User;
import ru.yandex.practicum.filmorate.service.UserService;
import ru.yandex.practicum.filmorate.storage.user.InMemoryUserStorage;

import java.util.List;

@RestController
@RequestMapping("/users")
@Slf4j
@RequiredArgsConstructor
public class UserController {
    private final InMemoryUserStorage inMemoryUserStorage;
    private final UserService userService;

    @PostMapping
    public User createUser(@Valid @RequestBody User user) {
        log.info("Создание пользователя: {}");
        return userService.createUser(user);
    }

    @PutMapping
    public User updateUser(@Valid @RequestBody User user) {
        log.info("Обновление информации о пользователе: {}");
        return userService.updateUser(user);
    }

    @GetMapping
    public List<User> getUsers() {
        log.info("Получить юзеров: {}");
        return inMemoryUserStorage.getUsers();
    }

    @GetMapping("/{userId}")
    public User getUser(@PathVariable(value = "userId") long userId) {
        log.info("Получить юзера по id: {}");
        return inMemoryUserStorage.getUserById(userId);
    }

    @PutMapping("/{userId}/friends/{friendId}")
    public User addFriend(@PathVariable(value = "userId") long userId,
                          @PathVariable(value = "friendId") long friendId) {
        log.info("Добавить друга: {}");
        return userService.addFriend(userId, friendId);
    }

    @GetMapping("/{userId}/friends")
    public List<User> getFriends(@PathVariable(value = "userId") long userId) {
        log.info("Получить друга: {}");
        return userService.getFriends(userId);
    }

    @DeleteMapping("/{userId}/friends/{friendId}")
    public User deleteFriend(@PathVariable(value = "userId") long userId,
                             @PathVariable(value = "friendId") long friendId) {
        log.info("Удалить из списка друзей юзера по id: {}");
        return userService.removeFriend(userId, friendId);
    }


    @GetMapping("/{userId}/friends/common/{otherId}")
    public List<User> getCommonFriends(@PathVariable(value = "userId") long userId,
                                       @PathVariable(value = "otherId") long otherId) {
        log.info("Получить список общих друзей: {}");
        return userService.commonFriend(userId, otherId);
    }
}
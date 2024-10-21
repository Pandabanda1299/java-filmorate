package ru.yandex.practicum.filmorate.storage.user;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.User;

import java.util.List;

@Component
public interface UserStorage {

    User getUserById(long id);

    List<User> getUsers();

    User addUser(@RequestBody User user);

    User updateUser(@RequestBody User user);
}
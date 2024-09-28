package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.controller.UserController;
import ru.yandex.practicum.filmorate.model.User;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class UserControllerTest {
    @Autowired
    private FilmController filmController;

    // Successfully adds a new user with valid data
    @Test
    public void test_add_user_success() {
        UserController userController = new UserController();
        User user = new User();
        user.setEmail("test@yandex.com");
        user.setLogin("testUser");
        user.setName("Test DMITRY");
        user.setPassword("password123");
        user.setBirthday(LocalDate.of(1998, 1, 1));

        User addedUser = userController.addUser(user);

        assertNotNull(addedUser.getId());
        assertEquals("test@yandex.com", addedUser.getEmail());
        assertEquals("testUser", addedUser.getLogin());
        assertEquals("Test DMITRY", addedUser.getName());
    }

    @Test
    public void update_user_with_valid_id_and_all_fields_provided() {
        UserController userController = new UserController();
        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setEmail("oldemail@yandex.ru");
        existingUser.setLogin("oldlogin");
        existingUser.setName("Old Name");
        existingUser.setPassword("oldpassword");
        existingUser.setBirthday(LocalDate.of(1990, 1, 1));
        userController.addUser(existingUser);

        User updatedUser = new User();
        updatedUser.setId(1L);
        updatedUser.setEmail("newemail@example.com");
        updatedUser.setLogin("newlogin");
        updatedUser.setName("New Name");
        updatedUser.setPassword("newpassword");
        updatedUser.setBirthday(LocalDate.of(1990, 1, 1));

        User result = userController.updateUser(updatedUser);

        assertEquals("newemail@example.com", result.getEmail());
        assertEquals("New Name", result.getName());
        assertEquals("newpassword", result.getPassword());
    }

}

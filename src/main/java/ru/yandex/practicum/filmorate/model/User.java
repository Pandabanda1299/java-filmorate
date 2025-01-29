package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.PastOrPresent;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Data
public class User {

    private long id; //целочисленный идентификатор
    @Email
    @NotBlank
    private String email; // электронная почта
    @NotBlank
    private String login; //логин пользователя
    private String name; //имя для отображения
    @PastOrPresent
    private LocalDate birthday;//дата рождения
    private Set<User> friends = new HashSet<>();
}

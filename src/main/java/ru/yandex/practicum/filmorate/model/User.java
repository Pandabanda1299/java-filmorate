package ru.yandex.practicum.filmorate.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

import java.time.LocalDate;
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
    private LocalDate birthday;//дата рождения
    private Set<Long> friends;
}

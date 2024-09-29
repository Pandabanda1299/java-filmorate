package ru.yandex.practicum.filmorate.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDate;

@Data
@EqualsAndHashCode(of = "email")
public class User {
    private Long id;
    private String login;
    private String name;
    private String email;
    private LocalDate birthday;
}

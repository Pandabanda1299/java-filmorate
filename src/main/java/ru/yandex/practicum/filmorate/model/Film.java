package ru.yandex.practicum.filmorate.model;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;


@Getter
@Setter
public class Film {
    private long id;
    private String name;
    private String description;
    private LocalDate releaseDate;
    private Integer duration;
}

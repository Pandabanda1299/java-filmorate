package ru.yandex.practicum.filmorate.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
public class Film {
    private long id; //целочисленный идентификатор
    @NotBlank(message = "Поле с названием фильма не должно быть пустым")
    private String name; //название
    @Size(max = 200, message = "Описание не должно превышать 200 символов")
    private String description; //описание
    private LocalDate releaseDate; //дата релиза
    @Positive(message = "Продолжительность фильма должна быть положительным числом")
    private Integer duration; //продолжительность фильма
    @JsonIgnore
    private Set<Long> likes = new HashSet<>();
    @JsonIgnore
    private long rate = 0;
    private List<Genre> genres;
    private Mpa mpa;

    public void addLike(long userId) {
        likes.add(userId);
        rate = likes.size();
    }

    public void removeLike(long userId) {
        likes.remove(userId);
        rate = likes.size();
    }
}


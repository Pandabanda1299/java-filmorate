package ru.yandex.practicum.filmorate.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/films")
public class FilmController {
    public static final Integer MAX_DESCRIPTION_LENGTH = 200;
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final HashMap<Long, Film> films = new HashMap<>();
    private static final Logger LOG = LoggerFactory.getLogger(FilmController.class);
    private static Long id = 0L;

    @PostMapping()
    public Film addFilm(@RequestBody Film film) {
        filmValidation(film);
        film.setId(++id);
        films.put(film.getId(), film);
        return film;
    }

    private static void filmValidation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            LOG.warn("Film name is blank");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null) {
            if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
                LOG.warn("Film description is too long");
                throw new ValidationException("Описание фильма не может быть больше " + MAX_DESCRIPTION_LENGTH + " символов");
            }
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            LOG.warn("Film release date is before minimum");
            throw new ValidationException("Дата релиза фильма не может быть раньше " + MIN_RELEASE_DATE);
        }
        if (film.getDuration() < 0) {
            LOG.warn("Film duration is negative");
            throw new ValidationException("Длительность фильма не может быть отрицательной");
        }
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            LOG.warn("Идентификатор фильма равен нулю.");
            throw new ValidationException("Id должен быть указан");
        }
        Long filmId = film.getId();
        if (films.containsKey(filmId)) {
            filmValidation(film);
            films.put(filmId, film);
            LOG.info("Film added");
            return film;
        }
        LOG.warn("Film not found");
        throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
    }


    @GetMapping
    public List<Film> getFilms() {
        LOG.info("Get all films");
        return new ArrayList<>(films.values());
    }
}
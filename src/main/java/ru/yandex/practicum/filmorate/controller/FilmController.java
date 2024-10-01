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
    private static final Logger LOG = LoggerFactory.getLogger(FilmController.class);
    private Long id = 0L;
    private final HashMap<Long, Film> films = new HashMap<>();

    @PostMapping()
    public Film addFilm(@RequestBody Film film) {
        filmValidation(film);
        film.setId(++id);
        films.put(film.getId(), film);
        return film;
    }

    private static void filmValidation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            LOG.warn("Название фильма пустое");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null) {
            if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
                LOG.warn("Описание фильма слишком длинное");
                throw new ValidationException("Описание фильма не может превышать " + MAX_DESCRIPTION_LENGTH + " символов");
            }
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            LOG.warn("Дата выхода фильма раньше минимума");
            throw new ValidationException("Дата релиза фильма не может быть раньше " + MIN_RELEASE_DATE);
        }
        if (film.getDuration() < 0) {
            LOG.warn("Продолжительность фильма отрицательная");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        LOG.info("Пришел PUT запрос /films с телом: {}", film);
        Long filmId = film.getId();
        if (films.containsKey(filmId)) {
            filmValidation(film);
            films.put(filmId, film);
            LOG.info("Фильм обновлен: {}", film);
            LOG.info("Отправлен ответ PUT /films с телом: {}", film);
            return film;
        }
        LOG.warn("Фильм не найден");
        throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
    }


    @GetMapping
    public List<Film> getFilms() {
        LOG.info("Пришел GET запрос /films");
        List<Film> filmList = new ArrayList<>(films.values());
        LOG.info("Отправлен ответ GET /films с телом: {}", filmList);
        return filmList;
    }
}
package ru.yandex.practicum.filmorate.storage.film;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {
    public static final Integer MAX_DESCRIPTION_LENGTH = 200;
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private Long id = 0L;
    private final HashMap<Long, Film> films = new HashMap<>();


    @Override
    public Film addFilm(@RequestBody Film film) {
        filmValidation(film);
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Фильм добавлен");
        return film;
    }


    @Override
    public Film updateFilm(@RequestBody Film film) {
        log.info("Пришел PUT запрос /films с телом: {}", film);
        Long filmId = film.getId();
        if (films.containsKey(filmId)) {
            filmValidation(film);
            films.put(filmId, film);
            log.info("Фильм обновлен: {}", film);
            log.info("Отправлен ответ PUT /films с телом: {}", film);
            return film;
        }
        log.warn("Фильм не найден");
        throw new NotFoundException("Фильм с id " + film.getId() + " не найден");
    }


    @Override
    public List<Film> getFilms() {
        log.info("Пришел GET запрос /films");
        List<Film> filmList = new ArrayList<>(films.values());
        log.info("Отправлен ответ GET /films с телом: {}", filmList);
        return filmList;
    }

    @Override
    public Film getFilmById(long id) {
        if (!films.containsKey(id)) {
            throw new NotFoundException("Фильм с таким id не найден  " + id);
        }
        log.info("Фильм по id ");
        return films.get(id);
    }

    private static void filmValidation(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Название фильма пустое");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null) {
            if (film.getDescription().length() > MAX_DESCRIPTION_LENGTH) {
                log.warn("Описание фильма слишком длинное");
                throw new ValidationException("Описание фильма не может превышать " + MAX_DESCRIPTION_LENGTH + " символов");
            }
        }
        if (film.getReleaseDate().isBefore(MIN_RELEASE_DATE)) {
            log.warn("Дата выхода фильма раньше минимума");
            throw new ValidationException("Дата релиза фильма не может быть раньше " + MIN_RELEASE_DATE);
        }
        if (film.getDuration() < 0) {
            log.warn("Продолжительность фильма отрицательная");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}
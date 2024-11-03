package ru.yandex.practicum.filmorate.storage.film;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


@Slf4j
@Component
public class InMemoryFilmStorage implements FilmStorage {

    private Long id = 0L;
    private final HashMap<Long, Film> films = new HashMap<>();


    @Override
    public Film addFilm(Film film) {
        film.setId(++id);
        films.put(film.getId(), film);
        log.info("Фильм добавлен");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        Long filmId = film.getId();
        if (films.containsKey(filmId)) {
            films.put(filmId, film);
            log.info("Фильм обновлен: {}", film);
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
}
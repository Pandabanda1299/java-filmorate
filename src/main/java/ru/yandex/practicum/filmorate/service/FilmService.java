package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.time.LocalDate;
import java.util.List;
//добавление популярных фильмов, добавление лайков и удаление.

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    public static final Integer MAX_DESCRIPTION_LENGTH = 200;
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final InMemoryFilmStorage filmStorage;
    private final UserStorage userStorage;


    public Film addFilm(Film film) {
        filmValidation(film);
        return filmStorage.addFilm(film);
    }

    public Film addLike(long userId, long filmId) {
        checkFilmAndUser(userId, filmId);
        Film film = filmStorage.getFilmById(filmId);
        film.addLike(userId);
        log.info("Вы поставили лайк ");
        return film;
    }

    public Film getFilmById(long filmId) {
        return filmStorage.getFilmById(filmId);
    }

    public Film updateFilm(Film film) {
        filmValidation(film);
        return filmStorage.updateFilm(film);
    }

    public List<Film> getFilms() {
        return filmStorage.getFilms();
    }

    public Film removeLike(long userId, long filmId) {
        checkFilmAndUser(userId, filmId);
        Film film = filmStorage.getFilmById(filmId);
        film.removeLike(userId);
        log.info("Вы убрали лайк ");
        return film;
    }

    public List<Film> popularFilm(Integer limit) {
        return filmStorage.getFilms()
                .stream()
                .sorted(((film1, film2) ->
                        Long.compare(film2.getRate(),
                                film1.getRate())))
                .limit(limit)
                .toList();
    }

    private void checkFilmAndUser(long userId, long filmId) {
        if (userStorage.getUserById(userId) == null) {
            throw new NotFoundException("Юзер с таким id не найден " + userId);
        }
        if (filmStorage.getFilmById(filmId) == null) {
            throw new NotFoundException("Фильм с таким id не найден " + filmId);
        }
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
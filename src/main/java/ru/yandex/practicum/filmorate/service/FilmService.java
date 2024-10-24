package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.storage.film.InMemoryFilmStorage;
import ru.yandex.practicum.filmorate.storage.user.UserStorage;

import java.util.List;
//добавление популярных фильмов, добавление лайков и удаление.

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    private final InMemoryFilmStorage filmStorage;
    private final UserStorage userStorage;

    public Film addLike(long userId, long filmId) {
        checkFilmAndUser(userId, filmId);
        Film film = filmStorage.getFilmById(filmId);
        film.getLike().add(userId);
        log.info("Вы поставили лайк ");
        return film;
    }

    public Film removeLike(long userId, long filmId) {
        checkFilmAndUser(userId, filmId);
        Film film = filmStorage.getFilmById(filmId);
        film.getLike().remove(userId);
        log.info("Вы убрали лайк ");
        return film;
    }

    public List<Film> popularFilm(Integer limit) {
        return filmStorage.getFilms()
                .stream()
                .sorted(((film1, film2) ->
                        Integer.compare(film2.getLike().size(),
                                film1.getLike().size())))
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

}
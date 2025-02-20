package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.LikeRepository;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.List;
//добавление популярных фильмов, добавление лайков и удаление.

@Slf4j
@Service
@RequiredArgsConstructor
public class FilmService {
    public static final Integer MAX_DESCRIPTION_LENGTH = 200;
    public static final LocalDate MIN_RELEASE_DATE = LocalDate.of(1895, 12, 28);
    private final FilmRepository filmRepository;
    private final MpaService mpaService;
    private final LikeRepository likeRepository;
    private final GenreRepository genreRepository;

    public Long addLike(long filmId, long userId) {
        return likeRepository.addLike(filmId, userId);
    }

    public Long removeLike(long filmId, long userId) {
        return likeRepository.removeLike(filmId, userId);
    }

    public List<Film> getPopularFilms(int count) {
        return filmRepository.getPopularFilms(count);
    }

    public Film addFilm(Film film) {
        filmValidation(film);
        mpaService.getMpaById(film.getMpa().getId());
        return filmRepository.create(film);
    }

    public Film getFilmById(Long filmId) {
        genreRepository.load();
        return filmRepository.findById(filmId);
    }

    public Film updateFilm(Film film) {
        filmValidation(film);
        return filmRepository.update(film);
    }


    public List<Film> getAllFilms() {
        final List<Film> films = filmRepository.findAll();
        genreRepository.load(films);
        return films;
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
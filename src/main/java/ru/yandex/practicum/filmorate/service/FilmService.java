package ru.yandex.practicum.filmorate.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.yandex.practicum.filmorate.dal.FilmRepository;
import ru.yandex.practicum.filmorate.dal.LikeRepository;
import ru.yandex.practicum.filmorate.dal.UserRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

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
    private final UserRepository userRepository;
    private final MpaService mpaService;
    private final GenreService genreService;
    private final LikeRepository likeRepository;

    public Long addLike(long filmId, long userId) {
     return  likeRepository.addLike(filmId, userId);
    }

    public Long removeLike(long filmId, long userId) {
        return likeRepository.removeLike(filmId, userId);
    }

    public List <Film> getPopularFilms(int count) {
      return filmRepository.getPopularFilms(count);
    }

    public Film addFilm(Film film) {
        filmValidation(film);
        mpaService.getMpaById(film.getMpa().getId());
//        for (Genre genre : film.getGenres()) {
//            genreService.getGenreById(genre.getId());
//        }
        return filmRepository.create(film);
    }

    public Film getFilmById(long filmId) {
        return filmRepository.findById(filmId);
    }

    public Film updateFilm(Film film) {
        filmValidation(film);
        return filmRepository.update(film);
    }

    public List<Film> getFilms() {
        return filmRepository.findAll();
    }

    private void checkFilmAndUser(long userId, long filmId) {
        if (userRepository.findById(userId) == null) {
            throw new NotFoundException("Юзер с таким id не найден " + userId);
        }
        if (filmRepository.findById(filmId) == null) {
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
package ru.yandex.practicum.filmorate.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.service.FilmService;

import java.util.List;

@Validated
@RestController
@RequestMapping("/films")
@Slf4j
@RequiredArgsConstructor
public class FilmController {


    private final FilmService filmService;

    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        log.info("Добавлен новый фильм: {}", film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info("Обновление фильма: {}");
        Film upFilm = filmService.updateFilm(film);
        log.info("Отправлен ответ PUT /films с телом: {}", film);
        return upFilm;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получение всех фильмов: {}");
        return filmService.getFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable(value = "filmId") long filmId) {
        log.info("Получение фильма по id: {}");
        return filmService.getFilmById(filmId);
    }

    @PutMapping("/{filmId}/like/{userId}")
    public Film addLike(@PathVariable(value = "userId") long userId, @PathVariable(value = "filmId") long filmId) {
        log.info("Добавление лайка к фильму с id: {}");
        return filmService.addLike(userId, filmId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public Film removeLike(@PathVariable(value = "userId") long userId, @PathVariable(value = "filmId") long filmId) {
        log.info("Удаление лайка с фильма по id: {}");
        return filmService.removeLike(userId, filmId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam(defaultValue = "10") Integer limit) {
        log.info("Получение популярных фильмов с лимитом: {}");
        return filmService.popularFilm(limit);
    }
}

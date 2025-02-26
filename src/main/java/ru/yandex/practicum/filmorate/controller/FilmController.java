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

    @PutMapping("/{filmId}/like/{userId}")
    public void addLike(@PathVariable() Long filmId, @PathVariable Long userId) {
        filmService.addLike(filmId, userId);
    }

    @DeleteMapping("/{filmId}/like/{userId}")
    public void deleteLike(@PathVariable() Long filmId, @PathVariable Long userId) {
        filmService.removeLike(filmId, userId);
    }


    @PostMapping
    public Film addFilm(@RequestBody @Valid Film film) {
        log.info("Добавлен новый фильм: {}", film);
        return filmService.addFilm(film);
    }

    @PutMapping
    public Film updateFilm(@RequestBody @Valid Film film) {
        log.info("Обновление фильма: {}", film);
        Film upFilm = filmService.updateFilm(film);
        log.info("Отправлен ответ PUT /films с телом: {}", film);
        return upFilm;
    }

    @GetMapping
    public List<Film> getAllFilms() {
        log.info("Получение всех фильмов: {}");
        return filmService.getAllFilms();
    }

    @GetMapping("/{filmId}")
    public Film getFilm(@PathVariable(value = "filmId") long filmId) {
        log.info("Получение фильма по id: {}", filmId);
        return filmService.getFilmById(filmId);
    }

    @GetMapping("/popular")
    public List<Film> getPopularFilms(@RequestParam int count) {
        List<Film> popular = filmService.getPopularFilms(count);
        log.info("Подсчет популярности " + popular);
        return popular;
    }
}

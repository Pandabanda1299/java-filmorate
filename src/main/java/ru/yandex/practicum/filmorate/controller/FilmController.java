package ru.yandex.practicum.filmorate.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/films")
public class FilmController {

    private static final Logger log = LoggerFactory.getLogger(FilmController.class);
    private final Map<Long, Film> films = new HashMap<>();
    private final AtomicLong idCounter = new AtomicLong();
    private static final LocalDate CINEMA_BIRTHDAY = LocalDate.of(1985, 12, 28);

    @GetMapping
    public List<Film> getFilms() {
        return films.values().stream().collect(Collectors.toList());
    }

    @PostMapping
    public Film addFilm(@RequestBody Film film) {
        validateFilm(film);
        film.setId(idCounter.incrementAndGet());
        films.put(film.getId(), film);
        log.info("Добавлен фильм с идентификатором {}", film.getId());
        return film;
    }

    @PutMapping
    public Film updateFilm(@RequestBody Film film) {
        if (!films.containsKey(film.getId())) {
            log.warn("Идентификатор фильма равен нулю.");
            throw new ValidationException("Id должен быть указан");
        }
        Film existingFilm = films.get(film.getId());
        if (existingFilm == null) {
            log.error("Фильм с идентификатором {} не найден", film.getId());
            throw new ValidationException("Фильм не найден");
        }
        validateFilm(film);
        existingFilm.setName(film.getName());
        existingFilm.setDescription(film.getDescription());
        existingFilm.setReleaseDate(film.getReleaseDate());
        existingFilm.setDuration(film.getDuration());
        log.info("Фильм с идентификатором {} обновлен.", film.getId());
        return existingFilm;
    }

    private void validateFilm(Film film) {
        if (film.getName() == null || film.getName().isEmpty()) {
            log.error("Название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription() != null && film.getDescription().length() > 200) {
            log.error("Описание фильма не может быть больше 200 символов");
            throw new ValidationException("Описание фильма не может превышать 200 символов");
        }
        if (film.getReleaseDate().isBefore(CINEMA_BIRTHDAY)) {
            log.error("Ошибка валидации: дата выпуска фильма не может быть ранее 28 декабря 1895 г.");
            throw new ValidationException("Дата релиза фильма не может быть раньше 1895-12-28");
        }
        if (film.getDuration() <= 0) {
            log.error("Продолжительность фильма должна быть положительным числом");
            throw new ValidationException("Продолжительность фильма должна быть положительным числом");
        }
    }
}

package ru.yandex.practicum.filmorate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.service.GenreService;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/genres")
public class GenreController {
    private final GenreService genreService;

    @GetMapping
    public List<GenreDto> getAllGenres() {
        log.info("Получен запрос GET /genres");
        List<GenreDto> genres = genreService.getAllGenres();
        log.info("Отправлен ответ GET /genres с количеством жанров: {}", genres);
        return genres;
    }

    @GetMapping("/{id}")
    public GenreDto getGenreById(@PathVariable Long id) {
        log.info("Получен запрос GET /genres/{}", id);
        GenreDto genre = genreService.getGenreById(id);
        log.info("Отправлен ответ GET /genres/{} с телом: {}", id, genre);
        return genre;
    }
}
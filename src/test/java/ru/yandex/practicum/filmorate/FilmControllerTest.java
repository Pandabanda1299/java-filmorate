package ru.yandex.practicum.filmorate;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.Duration;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
public class FilmControllerTest {
    @Autowired
    private FilmController filmController;

    @Test
    public void test_add_valid_film() {
        FilmController filmController = new FilmController();
        Film film = new Film();
        film.setName("начало");
        film.setDescription("Головокружительный триллер");
        film.setReleaseDate(LocalDate.of(2010, 7, 16));
        film.setDuration(Duration.ofMinutes(148));

        Film addedFilm = filmController.addFilm(film);

        assertNotNull(addedFilm.getId());
        assertEquals("начало", addedFilm.getName());
        assertEquals("Головокружительный триллер", addedFilm.getDescription());
        assertEquals(LocalDate.of(2010, 7, 16), addedFilm.getReleaseDate());
        assertEquals(Duration.ofMinutes(148), addedFilm.getDuration());
    }

    @Test
    public void test_update_film_success() {
        FilmController filmController = new FilmController();
        Film film = new Film();
        film.setId(1L);
        film.setName("Оригинальное имя");
        film.setDescription("Исходное описание");
        film.setReleaseDate(LocalDate.of(2000, 1, 1));
        film.setDuration(Duration.ofMinutes(120));

        filmController.addFilm(film);

        Film updatedFilm = new Film();
        updatedFilm.setId(1L);
        updatedFilm.setName("Обновленное имя");
        updatedFilm.setDescription("Обновленное описание");
        updatedFilm.setReleaseDate(LocalDate.of(2001, 1, 1));
        updatedFilm.setDuration(Duration.ofMinutes(130));

        Film result = filmController.updateFilm(updatedFilm);

        assertEquals("Обновленное имя", result.getName());
        assertEquals("Обновленное описание", result.getDescription());
        assertEquals(LocalDate.of(2001, 1, 1), result.getReleaseDate());
        assertEquals(Duration.ofMinutes(130), result.getDuration());
    }
}

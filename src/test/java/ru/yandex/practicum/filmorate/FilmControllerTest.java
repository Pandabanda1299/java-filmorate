package ru.yandex.practicum.filmorate;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.yandex.practicum.filmorate.controller.FilmController;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;

import java.time.LocalDate;

@SpringBootTest
public class FilmControllerTest {
    @Autowired
    private FilmController filmController;

    @Test
    public void ifFilmNameIsBlankThenThrowException() {
        Film film = new Film();
        film.setDuration(1);
        film.setName("");
        film.setReleaseDate(LocalDate.of(1994, 1, 1));
        Assertions.assertThrows(ConstraintViolationException.class, () -> filmController.addFilm(film));

    }

    @Test
    public void ifFilmDescriptionLengthIsMoreThen200ThenThrowException() {
        Film film = new Film();
        film.setDuration(1);
        film.setName("Побег из Шоушенка");
        film.setReleaseDate(LocalDate.of(1994, 1, 1));
        film.setDescription("Фильм «Побег из Шоушенка» (1994) — это культовая драма, снятая по мотивам повести Стивена Кинга. " +
                "В центре сюжета — история Энди Дюфрейна, банкира, несправедливо обвинённого в убийстве жены и приговорённого к пожизненному заключению в тюрьме Шоушенк. Несмотря на жестокость и безнадёжность тюремной жизни, " +
                "Энди не теряет надежды и находит способы выжить и даже обрести свободу. Фильм получил множество наград и считается одним из лучших в истории кинематографа.");
        Assertions.assertThrows(ConstraintViolationException.class, () -> filmController.addFilm(film));
    }

    @Test
    public void ifFilmReleaseDateIsBefore1895_12_28ThenThrowException() {
        Film film = new Film();
        film.setDuration(1);
        film.setName("Побег из Шоушенка");
        film.setReleaseDate(LocalDate.of(1794, 1, 1));
        Exception exception = Assertions.assertThrows(ValidationException.class, () -> filmController.addFilm(film));
        Assertions.assertEquals("Дата релиза фильма не может быть раньше 1895-12-28", exception.getMessage());
    }

    @Test
    public void ifFilmDurationIsNegativeThenThrowException() {
        Film film = new Film();
        film.setName("Побег из Шоушенка");
        film.setDuration(-1);
        film.setReleaseDate(LocalDate.of(1994, 1, 1));
        Exception exception = Assertions.assertThrows(ConstraintViolationException.class, () -> filmController.addFilm(film));
    }

}

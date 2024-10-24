package ru.yandex.practicum.filmorate.storage.film;

import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestBody;
import ru.yandex.practicum.filmorate.model.Film;

import java.util.List;

@Component
public interface FilmStorage {


    Film addFilm(@RequestBody Film film);

    Film updateFilm(@RequestBody Film film);

    List<Film> getFilms();

    Film getFilmById(long id);
}
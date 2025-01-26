package ru.yandex.practicum.filmorate.mapper;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import ru.yandex.practicum.filmorate.dto.GenreDto;
import ru.yandex.practicum.filmorate.dto.film.FilmDto;
import ru.yandex.practicum.filmorate.dto.filmDto.NewFilmRequest;
import ru.yandex.practicum.filmorate.dto.filmDto.FilmDto;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public final class FilmMapper {
    public static Film mapToFilm(NewFilmRequest newFilmRequest, Mpa mpa, List<Genre> genres) {
        Film film = new Film();
        film.setName(newFilmRequest.getName());
        film.setDescription(newFilmRequest.getDescription());
        film.setReleaseDate(newFilmRequest.getReleaseDate());
        film.setDuration(newFilmRequest.getDuration());
        film.setRate(newFilmRequest.getRate());
        film.setMpa(mpa);
        film.setGenres(genres);
        return film;
    }

    public static FilmDto mapToFilmDto(Film film) {

        FilmDto.setId(film.getId());
        FilmDto.setName(film.getName());
        FilmDto.setDescription(film.getDescription());
        FilmDto.setReleaseDate(film.getReleaseDate());
        FilmDto.setDuration(film.getDuration());

        Mpa mpa = film.getMpa();
        FilmDto.setMpa(GenreMapper.mapToMpaDto(mpa));

        if (Objects.nonNull(film.getGenres())) {
            List<GenreDto> genreDtos = film.getGenres()
                    .stream()
                    .map(GenreMapper::toGenreDto)
                    .collect(Collectors.toList());
            FilmDto.setGenres(genreDtos);
        }
        return FilmDto;
    }

    public static Film updateFilm(Film film, NewFilmRequest newFilmRequest, Mpa mpa, List<Genre> genres) {
        if (newFilmRequest.getName() != null) {
            film.setName(newFilmRequest.getName());
        }
        if (newFilmRequest.getDescription() != null) {
            film.setDescription(newFilmRequest.getDescription());
        }
        if (newFilmRequest.getReleaseDate() != null) {
            film.setReleaseDate(newFilmRequest.getReleaseDate());
        }
        if (newFilmRequest.getDuration() != null) {
            film.setDuration(newFilmRequest.getDuration());
        }
        if (newFilmRequest.getRate() != null) {
            film.setRate(newFilmRequest.getRate());
        }
        if (mpa != null) {
            film.setMpa(mpa);
        }
        if (genres != null) {
            film.setGenres(genres);
        }
        return film;
    }
}
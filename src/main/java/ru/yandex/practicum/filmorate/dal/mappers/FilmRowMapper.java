package ru.yandex.practicum.filmorate.dal.mappers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.dal.GenreRepository;
import ru.yandex.practicum.filmorate.dal.LikeRepository;
import ru.yandex.practicum.filmorate.dal.MpaRepository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class FilmRowMapper implements RowMapper<Film> {

    private final MpaRepository mpaRepository;
    private final GenreRepository genreRepository;
    private final LikeRepository likeRepository;

    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        log.info("НАЧАЛО FilmRowMapper mapRow");
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        film.setReleaseDate(rs.getDate("releaseDate").toLocalDate());
        film.setDuration(rs.getInt("duration"));


        Long mpaId = rs.getLong("rating_id");
        Mpa mpa = mpaRepository.getMpaById(mpaId, () -> new NotFoundException("MPA с id " + mpaId + " не найден"));
        film.setMpa(mpa);
        List<Genre> genresForFilm = genreRepository.getGenresForFilm(film.getId());
        film.setGenres(genresForFilm);
        Set<Integer> likes = likeRepository.getLikes(film.getId());
        film.setLikes(likes);

        return film;
    }
}
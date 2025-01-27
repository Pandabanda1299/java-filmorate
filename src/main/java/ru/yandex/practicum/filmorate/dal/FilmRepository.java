package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dto.GenreDto;

import ru.yandex.practicum.filmorate.dto.filmDto.NewFilmRequest;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

@Repository
public class FilmRepository extends BaseRepository<Film> {
    private static final String FIND_ALL_FILMS = "SELECT f.*, r.ID AS mpa_id, r.name AS mpa_name " +
            "FROM films f " +
            "JOIN RATING r ON f.RATING_ID = r.ID";

    private static final String FIND_FILM_BY_ID = "SELECT f.*, m.mpa_id AS mpa_id, m.name AS mpa_name " +
            "FROM films f " +
            "JOIN mpa m ON f.mpa_id = m.mpa_id " +
            "WHERE f.film_id = ?";

    private static final String CREATE_FILM_GENRES = "INSERT INTO FILM_GENRE (film_id, genre_id) VALUES (?, ?)";
    private static final String GET_POPULAR_FILMS = "SELECT f.*, r.ID AS mpa_id, r.name AS mpa_name " +
            "FROM films f " +
            "JOIN RATING r ON f.RATING_ID = r.ID " +
            "ORDER BY f.RATING_ID DESC LIMIT ?";

    private static final String CREATE_FILM = "INSERT INTO films (" +
            "name, description, release_date, duration, mpa_id)" +
            " VALUES (?, ?, ?, ?, ?)";

    public FilmRepository(JdbcTemplate jdbcTemplate, RowMapper<Film> filmRowMapper) {
        super(jdbcTemplate, filmRowMapper, Film.class);
    }

    public List<Film> findAll() {
        return jdbc.query(FIND_ALL_FILMS, mapper);
    }

    public Optional<Film> findById(long id) {
        return findOne(FIND_FILM_BY_ID, id);
    }

    public Film create(Film film) {
        long id = insert(CREATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        Set<Genre> uniqueGenres = new HashSet<>(film.getGenres());

        for (Genre genre : uniqueGenres) {
            jdbc.update(CREATE_FILM_GENRES, id, genre.getId());
        }
        return film;
    }

    public void update(NewFilmRequest film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, RELEASEDATE = ?, " +
                "duration = ?, RATING_ID = ? WHERE ID = ?";
        try {
            jdbc.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                    film.getDuration(), film.getRate(), film.getMpa().getId(), film.getId());
        } catch (Exception e) {
            throw e;
        }
        saveGenres(film);
    }

    private void saveGenres(NewFilmRequest film) {
        final Integer filmId = film.getId();
        jdbc.update("DELETE FROM FILM_GENRE WHERE FILM_ID = ?", filmId);
        final List<GenreDto> genres = film.getGenres();
        if (genres == null || genres.isEmpty()) {
            return;
        }
        final ArrayList<GenreDto> genreList = new ArrayList<>(genres);
        jdbc.batchUpdate(CREATE_FILM_GENRES, new BatchPreparedStatementSetter() {
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setLong(1, filmId);
                ps.setLong(2, genreList.get(i).getId());
            }

            public int getBatchSize() {
                return genreList.size();
            }
        });
    }

    public List<Film> getPopularFilms(int count) {
        return jdbc.query(GET_POPULAR_FILMS, mapper, count);
    }
}

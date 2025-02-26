package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.function.UnaryOperator.identity;

@Repository
public class GenreRepository extends BaseRepository<Genre> {

    private static final String FIND_ALL_GENRES = "SELECT * FROM GENRE ORDER BY ID";
    private static final String FIND_GENRE_BY_ID = "SELECT * FROM GENRE WHERE id = ?";
    private static final String FIND_GENRES_FOR_FILM = "SELECT g.* FROM GENRE g " +
            "JOIN FILM_GENRE fg ON g.ID = fg.genre_id " +
            "WHERE fg.film_id = ?";
    private static final String UPDATE_GENRE = "INSERT INTO FILM_GENRE (film_id, id) VALUES (?, ?)";
    private static final String DELETE = "DELETE FROM FILM_GENRE WHERE film_id = ?";

    public GenreRepository(JdbcTemplate jdbc, RowMapper<Genre> mapper) {
        super(jdbc, mapper, Genre.class);
    }

    public List<Genre> findAll() {
        return jdbc.query(FIND_ALL_GENRES, mapper);
    }

    public Optional<Genre> getGenreById(Long id) {
        return findOne(FIND_GENRE_BY_ID, id);
    }


    public List<Genre> getGenresForFilm(long filmId) {
        String sql = "SELECT g.* FROM GENRE g " +
                "JOIN FILM_GENRE fg ON g.ID = fg.genre_id " +
                "WHERE fg.film_id = ?";

        try {
            return jdbc.query(sql, new GenreRowMapper(), filmId);
        } catch (Exception e) {
            throw new ValidationException("Отсуствует жанр "); // Возвращаем пустой список, если данных нет
        }
    }

    public void update(long filmId, List<Long> genreIds) {
        jdbc.update(DELETE, filmId);
        for (Long genreId : genreIds) {
            jdbc.update(UPDATE_GENRE, filmId, genreId);
        }
    }

    public void load(List<Film> films) {
        String inSql = String.join(",", Collections.nCopies(films.size(), "?"));
        final Map<Long, Film> filmById = films.stream().collect(Collectors.toMap(Film::getId, identity()));
        final String sqlQuery = "select * from GENRE g, FILM_GENRE fg where fg.GENRE_ID = g.ID AND fg.FILM_ID in (" + inSql + ")";
        jdbc.query(sqlQuery, (rs) -> {
            final Film film = filmById.get(rs.getLong("FILM_ID"));
            film.getGenres().add(makeGenre(rs, 0));
        }, films.stream().map(Film::getId).toArray());
    }


    static Genre makeGenre(ResultSet rs, int rowNum) throws SQLException {
        return new Genre(
                rs.getLong("genre_id"),
                rs.getString("name"));
    }
}
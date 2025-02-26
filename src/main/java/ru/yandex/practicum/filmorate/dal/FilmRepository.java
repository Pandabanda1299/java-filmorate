package ru.yandex.practicum.filmorate.dal;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


@Slf4j
@Repository
public class FilmRepository extends BaseRepository<Film> {
    private static final String FIND_ALL_FILMS = "SELECT * FROM FILMS f, RATING m WHERE f.RATING_ID = m.ID";
    private static final String FIND_FILM_BY_ID = "SELECT * FROM FILMS f, RATING m WHERE f.RATING_ID = m.ID AND f.ID = ?";
    private static final String CREATE_FILM_GENRES = "INSERT INTO FILM_GENRE (film_id, genre_id) VALUES (?, ?)";
    private static final String GET_POPULAR_FILMS = "SELECT f.*, COUNT(l.USER_ID) AS likes_count " +
            "FROM FILMS f " +
            "LEFT JOIN LIKES l ON f.ID = l.FILM_ID " +
            "GROUP BY f.ID " +
            "ORDER BY likes_count DESC LIMIT ?";

    private static final String CREATE_FILM = "INSERT INTO films (" +
            "name, description, releaseDate, duration, rating_id)" +
            " VALUES (?, ?, ?, ?, ?)";

    private static final String filmLikesQueryCount = "SELECT f.id, f.name, f.description," +
            " f.releaseDate," +
            " f.duration, f.mpa_id " +
            "FROM films AS f " +
            "LEFT OUTER JOIN film_like AS fl ON f.id = fl.film_id " +
            "GROUP BY f.id " +
            "ORDER BY COUNT(fl.film_id) DESC " +
            "LIMIT ?";

    private final JdbcTemplate jdbcTemplate;
    private final GenreRepository genreRepository;


    public FilmRepository(RowMapper<Film> filmRowMapper, JdbcTemplate jdbcTemplate, GenreRepository genreRepository) {
        super(jdbcTemplate, filmRowMapper, Film.class);
        this.jdbcTemplate = jdbcTemplate;
        this.genreRepository = genreRepository;
    }

    public List<Film> findAll() {
        return jdbc.query(FIND_ALL_FILMS, mapper);
    }

    public Film findById(long id) {
        log.info("Получение фильма по айди {}", id);
        return jdbcTemplate.queryForObject(FIND_FILM_BY_ID, mapper, id);
    }

    public Film create(Film film) {
        log.info(film.toString());
        genreRepository.getGenresForFilm(film.getId());
        long id = insert(CREATE_FILM,
                film.getName(),
                film.getDescription(),
                film.getReleaseDate(),
                film.getDuration(),
                film.getMpa().getId()
        );
        film.setId(id);
        List<Genre> uniqueGenres = new ArrayList<>();
        if (film.getGenres() != null) {
            uniqueGenres = film.getGenres().stream().distinct().toList();
        }
        for (Genre genre : uniqueGenres) {
            Long genreId = genre.getId();
            if (genreId < 1 || genreId > 6) {
                throw new NotFoundException("Жанр не найден ");
            }
            jdbc.update(CREATE_FILM_GENRES, id, genre.getId());
        }
        return film;
    }

    public Film update(Film film) {
        String sqlQuery = "UPDATE films SET name = ?, description = ?, RELEASEDATE = ?, " +
                "duration = ?, RATING_ID = ? WHERE ID = ?";
        try {
            log.info("Обновление фильма в блоке трай: " + film);
            jdbc.update(sqlQuery, film.getName(), film.getDescription(), film.getReleaseDate(),
                    film.getDuration(), film.getMpa().getId(), film.getId());
        } catch (Exception e) {
            throw e;
        }
        return findById(film.getId());
    }

    private void saveGenres(Film film) {
        final Long filmId = film.getId();
        jdbc.update("DELETE FROM FILM_GENRE WHERE FILM_ID = ?", filmId);
        final List<Genre> genres = film.getGenres();
        if (genres == null || genres.isEmpty()) {
            return;
        }
        final ArrayList<Genre> genreList = new ArrayList<>(genres);
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

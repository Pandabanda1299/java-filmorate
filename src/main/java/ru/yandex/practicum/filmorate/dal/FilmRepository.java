    package ru.yandex.practicum.filmorate.dal;

    import lombok.extern.slf4j.Slf4j;
    import org.springframework.jdbc.core.BatchPreparedStatementSetter;
    import org.springframework.jdbc.core.JdbcTemplate;
    import org.springframework.jdbc.core.RowMapper;
    import org.springframework.stereotype.Repository;
    import ru.yandex.practicum.filmorate.model.Film;
    import ru.yandex.practicum.filmorate.model.Genre;

    import java.sql.PreparedStatement;
    import java.sql.SQLException;
    import java.util.ArrayList;
    import java.util.HashSet;
    import java.util.List;
    import java.util.Set;


    @Slf4j
    @Repository
    public class FilmRepository extends BaseRepository<Film> {
        private static final String FIND_ALL_FILMS = "SELECT f.*, r.ID AS rating_id, r.name AS mpa_name " +
                "FROM films f " +
                "JOIN RATING r ON f.RATING_ID = r.ID";

        private static final String FIND_FILM_BY_ID = "SELECT films.*, FILM_GENRE.GENRE_ID AS genre_id " +
                "FROM films JOIN FILM_GENRE ON films.id = FILM_GENRE.FILM_ID  WHERE films.id = ?";


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

        public FilmRepository(RowMapper<Film> filmRowMapper, JdbcTemplate jdbcTemplate) {
            super(jdbcTemplate, filmRowMapper, Film.class);
            this.jdbcTemplate = jdbcTemplate;
        }

        public List<Film> findAll() {
            return jdbc.query(FIND_ALL_FILMS, mapper);
        }

        public Film findById(long id) {
            return jdbcTemplate.queryForObject(FIND_FILM_BY_ID, mapper, id);
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

//        private void saveGenres(Film film) {
//            final Long filmId = film.getId();
//            jdbc.update("DELETE FROM FILM_GENRE WHERE FILM_ID = ?", filmId);
//            final List<Genre> genres = film.getGenres();
//            if (genres == null || genres.isEmpty()) {
//                return;
//            }
//            final ArrayList<Genre> genreList = new ArrayList<>(genres);
//            jdbc.batchUpdate(CREATE_FILM_GENRES, new BatchPreparedStatementSetter() {
//                public void setValues(PreparedStatement ps, int i) throws SQLException {
//                    ps.setLong(1, filmId);
//                    ps.setLong(2, genreList.get(i).getId());
//                }
//
//                public int getBatchSize() {
//                    return genreList.size();
//                }
//            });
//        }

        public List<Film> getPopularFilms(int count) {
            return jdbc.query(GET_POPULAR_FILMS, mapper, count);
        }
    }

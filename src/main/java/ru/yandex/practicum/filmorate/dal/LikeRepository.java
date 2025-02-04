package ru.yandex.practicum.filmorate.dal;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.ILoggerFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.dal.mappers.LikeRowMapper;
import ru.yandex.practicum.filmorate.model.Likes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Repository
@RequiredArgsConstructor
public class LikeRepository {

    private static final String ADD_LIKE = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String REMOVE_LIKE = "DELETE FROM likes WHERE film_id = ? AND user_id = ?";
    private static final String GET_LIKES = "SELECT * FROM likes WHERE film_id = ?";


    private final JdbcTemplate jdbcTemplate;
    private final RowMapper <Likes> likeRowMapper;

    public Long addLike(long filmId, long userId) {
        log.info("Adding like to " + filmId + " by user " + userId);
        jdbcTemplate.update(ADD_LIKE, filmId, userId);
        return filmId;
    }

    public Long removeLike(long filmId, long userId) {
        log.info("Removing like from " + filmId + " by user " + userId);
        jdbcTemplate.update(REMOVE_LIKE, filmId, userId);
        return filmId;
    }

    public Set<Integer> getLikes(long filmId) {
        Set<Integer> userId = new HashSet<>(jdbcTemplate.query(GET_LIKES, new RowMapper<Integer>() {
            @Override
            public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
                return rs.getInt("user_id");
            }
        },filmId));
        return userId;
    }
    //ОТЬЕБНУЛИ ЖАНРЫ НАДО ФИКСИТЬ СУКА БЛЯТЬ
}
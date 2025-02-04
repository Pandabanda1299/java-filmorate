package ru.yandex.practicum.filmorate.dal.mappers;

import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Likes;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class LikeRowMapper implements RowMapper <Likes> {

    @Override
    public Likes mapRow(ResultSet rs, int rowNum) throws SQLException {
        return Likes.builder()
                .id(rs.getInt("id"))
                .likeUserId(rs.getInt("user_id"))
                .filmId(rs.getInt("film_id"))
                .build();
    }
}

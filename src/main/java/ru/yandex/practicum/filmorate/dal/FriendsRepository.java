package ru.yandex.practicum.filmorate.dal;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.model.User;

import java.util.ArrayList;
import java.util.List;

@Repository
public class FriendsRepository {

    private static final String ADD_FRIEND = "INSERT INTO friends (user_id, FRIEND_USER_ID) VALUES (?, ?)";
    private static final String DELETE_FRIEND = "DELETE FROM friends WHERE user_id = ? AND FRIEND_USER_ID = ?";
    private static final String GET_FRIENDS = "SELECT u.* FROM users u JOIN friends f ON u.ID = f.FRIEND_USER_ID" +
            " WHERE f.user_id = ?";
    private static final String GET_COMMON_FRIENDS = "SELECT u.* FROM users u " +
            "JOIN friends f1 ON u.ID= f1.FRIEND_USER_ID " +
            "JOIN friends f2 ON u.ID = f2.FRIEND_USER_ID " +
            "WHERE f1.user_id = ? AND f2.user_id = ?";

    private final JdbcTemplate jdbcTemplate;
    private final RowMapper<User> mapper;
    private final UserRepository userRepository;

    public FriendsRepository(JdbcTemplate jdbcTemplate, RowMapper<User> mapper, UserRepository userRepository) {
        this.jdbcTemplate = jdbcTemplate;
        this.mapper = mapper;
        this.userRepository = userRepository;
    }

    public User addFriend(long userId, long friendId) {
        User user = userRepository.findById(userId);
        User user2 = userRepository.findById(friendId);
        jdbcTemplate.update(ADD_FRIEND, userId, friendId);
        user.getFriends().add(user2);
        return user;
    }

    public User deleteFriend(long userId, long friendId) {
        jdbcTemplate.update(DELETE_FRIEND, userId, friendId);
        return userRepository.findById(userId);
    }

    public List<User> getFriends(Long userId) {
        String sqlQueryUser2 = "SELECT FRIEND_USER_ID " +
                "FROM FRIENDS WHERE USER_ID = ?";

        List<Long> friendsId = jdbcTemplate.queryForList(sqlQueryUser2, Long.class, userId);
        List<User> allUsers = userRepository.findAll().stream().toList();
        List<User> result = new ArrayList<>();

        for (User user : allUsers) {
            if (friendsId.contains(user.getId())) {
                result.add(user);
            }
        }

        return result;
    }


    public List<User> getCommonFriends(long userId, long otherUserId) {
        return jdbcTemplate.query(GET_COMMON_FRIENDS, mapper, userId, otherUserId);
    }
}
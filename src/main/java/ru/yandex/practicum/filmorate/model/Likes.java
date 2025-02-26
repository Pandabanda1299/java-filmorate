package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class Likes {

    private int id;
    private int likeUserId;
    private int filmId;

}

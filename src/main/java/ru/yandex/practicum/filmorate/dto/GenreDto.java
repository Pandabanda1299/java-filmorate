package ru.yandex.practicum.filmorate.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class GenreDto {
    private Long id;
    private String name;
}
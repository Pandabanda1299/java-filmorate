package ru.yandex.practicum.filmorate.model;

import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.params.provider.ArgumentsSource;

@Data
@Builder
@RequiredArgsConstructor
public class Mpa {
    private Integer id;
    private String name;
}

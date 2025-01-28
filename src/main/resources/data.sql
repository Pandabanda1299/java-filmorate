-- Заполнение таблицы users
INSERT INTO users (name, email, login, birthday)
VALUES ('Иван Иванов', 'ivan@example.com', 'ivan_ivanov', '1990-01-15'),
       ('Мария Петрова', 'maria@example.com', 'maria_petrova', '1985-05-22'),
       ('Алексей Сидоров', 'alexey@example.com', 'alexey_sidorov', '1995-11-30');

-- Заполнение таблицы status
INSERT INTO status (name)
VALUES ('Pending'),
       ('Accepted'),
       ('Rejected');

-- Заполнение таблицы rating
MERGE INTO rating AS r
    USING (VALUES
               ('G'),
               ('PG'),
               ('PG-13'),
               ('R'),
               ('NC-17')
        ) AS source(name)
ON r.name = source.name
WHEN NOT MATCHED THEN
    INSERT (name) VALUES (source.name);

-- Заполнение таблицы genre
INSERT INTO genre (name)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Документальный');

-- Заполнение таблицы films
INSERT INTO films (name, description, releaseDate, duration, rating_id)
VALUES ('Фильм 1', 'Описание фильма 1', '2020-01-01', 120, 1),
       ('Фильм 2', 'Описание фильма 2', '2021-05-15', 90, 2),
       ('Фильм 3', 'Описание фильма 3', '2022-10-30', 150, 3);

-- Заполнение таблицы filmGenre
INSERT INTO FILM_GENRE (film_id, genre_id)
VALUES (1, 1), -- Фильм 1 - Комедия
       (1, 2), -- Фильм 1 - Драма
       (2, 3), -- Фильм 2 - Мультфильм
       (3, 4); -- Фильм 3 - Триллер


-- Заполнение таблицы friends
INSERT INTO friends (user_id, friend_user_id, status_id)
VALUES (1, 2, 2), -- Иван и Мария - Accepted
       (1, 3, 1), -- Иван и Алексей - Pending
       (2, 3, 3); -- Мария и Алексей - Rejected

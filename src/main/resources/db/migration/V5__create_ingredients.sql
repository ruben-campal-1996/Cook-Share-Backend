CREATE table ingredients (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(60) UNIQUE NOT NULL,
    description TEXT NULL,
    image_url TEXT NOT NULL DEFAULT './images/avatar_defaultUrl.png',
    calories_per_100g NUMERIC(5,2) NULL
)
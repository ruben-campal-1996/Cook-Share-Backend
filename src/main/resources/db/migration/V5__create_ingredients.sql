CREATE table ingredients (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(60) UNIQUE NOT NULL,
    description TEXT NULL,
    image_url TEXT NOT NULL,
    calories_per_100g INTEGER NULL
)
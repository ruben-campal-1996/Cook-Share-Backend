CREATE TABLE recipe_rating (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    recipe_id INTEGER NOT NULL,
    user_id INTEGER NOT NULL,
    score SMALLINT NOT NULL CHECK (score BETWEEN 0 AND 10),
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_recipe_rating_recipe
        FOREIGN KEY (recipe_id)
        REFERENCES recipe(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_recipe_rating_user
        FOREIGN KEY (user_id)
        REFERENCES users_details(user_id)
        ON DELETE CASCADE,

    CONSTRAINT uq_recipe_rating_unique
        UNIQUE (recipe_id, user_id)
);
CREATE TABLE recipe (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(155) NOT NULL,
    description TEXT NULL,
    instructions TEXT NULL,
    image_url TEXT NOT NULL,
    servings INTEGER NOT NULL,
    author_id INTEGER NOT NULL,
    status_id INTEGER NOT NULL,
    total_calories INTEGER NULL,
    calories_per_serving INTEGER NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status BOOLEAN NOT NULL DEFAULT TRUE,
    updated_at TIMESTAMP NOT NULL,
    published_at TIMESTAMP NULL,
    reviewed_at TIMESTAMP NULL,
    reviewed_by INTEGER NULL,

    CONSTRAINT fk__recipe_author
        FOREIGN KEY (author_id)
        REFERENCES users_details(user_id),

    CONSTRAINT fk_recipe_status_id_status
        FOREIGN KEY (status_id)
        REFERENCES recipe_status(id), 
    
    CONSTRAINT fk_recipe_reviewer
        FOREIGN KEY (reviewed_by)
        REFERENCES users_details(user_id)
)
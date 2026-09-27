CREATE TABLE favourite (
    id_recipe INTEGER NOT NULL,
    id_user INTEGER NOT NULL,

     CONSTRAINT pk_favourite
        PRIMARY KEY (id_recipe, id_user),

    CONSTRAINT fk_favourite_recipe
        FOREIGN KEY (id_recipe)
        REFERENCES recipe(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_favourite_user
        FOREIGN KEY (id_user)
        REFERENCES users_details(user_id)
)
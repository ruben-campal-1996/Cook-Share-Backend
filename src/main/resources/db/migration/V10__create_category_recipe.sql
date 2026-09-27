CREATE TABLE category_recipe (
    id_category INTEGER NOT NULL,
    id_recipe INTEGER NOT NULL,

    CONSTRAINT pk_category_recipe
        PRIMARY KEY (id_category, id_recipe),

    CONSTRAINT fk_category_recipe_category
        FOREIGN KEY (id_category)
        REFERENCES categories(id),

    CONSTRAINT fk_category_recipe_recipe
        FOREIGN KEY (id_recipe)
        REFERENCES recipe(id)
        ON DELETE CASCADE
)
CREATE TABLE favourite (
    id_recipe INTEGER UNIQUE NOT NULL,
    id_user INTEGER NOT NULL

    CONSTRAINT pk__category_recipe
        PRIMARY KEY(id_category, id_recipe),
    
    CONSTRAINT fk__category_recipe_category
        FOREIGN KEY(id_category)
        REFERENCES categories(id),

    CONSTRAINT fk__category_recipe_recipe
        FOREIGN KEY (id_recipe)
        REFERENCES recipe(id)
)